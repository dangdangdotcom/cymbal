package com.dangdang.cymbal.service.operation.service.process.impl;

import com.dangdang.cymbal.common.enums.CapacityUnit;
import com.dangdang.cymbal.common.util.CollectionUtil;
import com.dangdang.cymbal.domain.bo.ClusterBO;
import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.Cluster;
import com.dangdang.cymbal.domain.po.ClusterScale;
import com.dangdang.cymbal.domain.po.Node;
import com.dangdang.cymbal.domain.po.RedisMode;
import com.dangdang.cymbal.domain.po.RedisReplicationRole;
import com.dangdang.cymbal.domain.po.ScaleResult;
import com.dangdang.cymbal.domain.po.ScaleStatus;
import com.dangdang.cymbal.domain.po.ScaleType;
import com.dangdang.cymbal.service.cluster.service.entity.ClusterEntityService;
import com.dangdang.cymbal.service.cluster.service.entity.InstanceEntityService;
import com.dangdang.cymbal.service.cluster.service.process.InstanceProcessService;
import com.dangdang.cymbal.service.cluster.service.utility.DeploymentUtilityService;
import com.dangdang.cymbal.service.constant.Constant;
import com.dangdang.cymbal.service.monitor.service.MonitorService;
import com.dangdang.cymbal.service.node.exception.NotEnoughResourcesException;
import com.dangdang.cymbal.service.node.service.entity.NodeEntityService;
import com.dangdang.cymbal.service.operation.enums.RedisConfigItem;
import com.dangdang.cymbal.service.operation.exception.ScaleException;
import com.dangdang.cymbal.service.operation.service.entity.ClusterScaleEntityService;
import com.dangdang.cymbal.service.operation.service.process.ClusterScaleProcessService;
import com.dangdang.cymbal.service.operation.service.process.ConfigProcessService;
import com.dangdang.cymbal.service.operation.service.utility.RedisClientUtilityService;
import com.google.common.base.Preconditions;
import io.lettuce.core.MigrateArgs;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.SlotHash;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.NodeSelection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import io.lettuce.core.cluster.models.partitions.RedisClusterNode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Implement of {@link ClusterScaleProcessService}.
 *
 * @author GeZhen
 */
@Slf4j
@Service
public class ClusterScaleProcessServiceImpl implements ClusterScaleProcessService {

    // 扩容中各步骤的最大重试次数
    private static final int ENLARGE_MAX_ATTEMPTS = 3;

    // 平衡因数，槽数差值小于此值，不参与移槽
    private static final int BALANCE_THRESHOLD = 10;

    // redis访问的超时秒数
    private static final long DEFAULT_TIMEOUT = 10;

    private static final int KEYS_PER_MIGRATE = 1000;

    private static final long MIGRATE_TIMEOUT = 10000;

    private static final long MIGRATE_BACKOFF_PERIOD = 100;

    private static final int MAX_RESULT_DESC_LENGTH = 2048;

    @Resource
    private ClusterEntityService clusterEntityService;

    @Resource
    private ClusterScaleEntityService redisClusterScaleEntityService;

    @Resource
    private InstanceEntityService instanceEntityService;

    @Resource
    private InstanceProcessService instanceProcessService;

    @Resource
    private DeploymentUtilityService deploymentUtilityService;

    @Resource
    private ConfigProcessService redisConfigProcessService;

    @Resource
    private MonitorService monitorService;

    @Resource
    private NodeEntityService nodeEntityService;

    @Resource
    private RedisClientUtilityService redisClientUtilityService;

    private ExecutorService processThreadPool = new ThreadPoolExecutor(0,
            Runtime.getRuntime().availableProcessors() * 2, 0L, TimeUnit.MILLISECONDS,
            new SynchronousQueue<Runnable>());

    private RetryTemplate retryTemplate = new RetryTemplate();

    public ClusterScaleProcessServiceImpl() {
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(ENLARGE_MAX_ATTEMPTS);
        retryTemplate.setRetryPolicy(retryPolicy);

        // 失败后补偿策略
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(MIGRATE_BACKOFF_PERIOD);
        retryTemplate.setBackOffPolicy(backOffPolicy);
    }

    @Override
    @Transactional
    public List<InstanceBO> doScale(final ClusterScale clusterScale) {
        Cluster cluster = clusterEntityService.getByClusterId(clusterScale.getClusterId());
        redisClusterScaleEntityService.save(clusterScale);

        List<InstanceBO> newInstances = null;
        switch (clusterScale.getType()) {
            case HORIZONTAL:
                newInstances = horizontalScale(cluster, clusterScale);
                break;
            case VERTICAL:
                this.verticalScale(cluster, clusterScale);
                newInstances = Collections.emptyList();
                break;
            case SLAVE_ONLY:
                newInstances = this.addSlaveToEveryMaster(cluster, clusterScale);
                break;
            default:
                break;
        }
        return newInstances;
    }

    private Cluster createScaleRedisCluster(final Cluster presentCluster) {
        Cluster scaleCluster = new Cluster();
        BeanUtils.copyProperties(presentCluster, scaleCluster);
        scaleCluster.setId(null);
        scaleCluster.setStatus(null);
        scaleCluster.setCreationDate(new Date());
        scaleCluster.setLastChangedDate(scaleCluster.getLastChangedDate());
        return scaleCluster;
    }

    private List<InstanceBO> horizontalScale(final Cluster presentCluster, final ClusterScale clusterScale) {
        Cluster scaleCluster = createScaleRedisCluster(presentCluster);
        scaleCluster.setMasterCount(clusterScale.getScaleNum());

        List<InstanceBO> scaledInstances = instanceProcessService.createInstances(scaleCluster);

        if (RedisMode.CLUSTER.equals(scaleCluster.getRedisMode())) {
            this.horizontalScaleForCluster(scaledInstances, presentCluster, clusterScale);
        } else {
            this.horizontalScaleForStandalone(scaledInstances, presentCluster, clusterScale);
        }

        initMonitorAndConfig(scaledInstances);
        presentCluster.setMasterCount(presentCluster.getMasterCount() + clusterScale.getScaleNum());
        clusterEntityService.updateById(presentCluster);

        return scaledInstances;
    }

    /**
     * Do horizontal scale for cluster.
     * Steps:
     * 1. deploy new redis instances(deploy, startup, meet, replicate).
     * 2. effect redis config for those instances.
     * 3. query and update cluster node id for those instances.
     * 4. init monitor for those instances.
     * 5. Do rebalance(migrate slot) for cluster.
     *
     * @param newInstanceBOs instance to scale
     * @param cluster cluster
     * @param clusterScale cluster scale
     */
    private void horizontalScaleForCluster(final List<InstanceBO> newInstanceBOs, final Cluster cluster,
            final ClusterScale clusterScale) {
        List<InstanceBO> presentServerInstances = instanceProcessService
                .queryInstanceBOsByClusterId(cluster.getClusterId());
        ClusterBO redisClusterBO = ClusterBO.builder().cluster(cluster).instanceBOs(newInstanceBOs).build();
        deploymentUtilityService
                .deployForScaleOfCluster(redisClusterBO, CollectionUtil.getFirst(presentServerInstances));
        instanceProcessService.queryAndUpdateRedisClusterNodeId(redisClusterBO);
        rebalanceClusterAsync(cluster, clusterScale);
    }

    // TODO: Scale detail for sentinel server.
    private void horizontalScaleForStandalone(final List<InstanceBO> newInstanceBOs, final Cluster cluster,
            final ClusterScale clusterScale) {
        ClusterBO redisClusterBO = ClusterBO.builder().cluster(cluster).instanceBOs(newInstanceBOs).build();
        deploymentUtilityService.deployForScaleOfStandalone(redisClusterBO);
        updateRedisClusterScaleToSuccess(clusterScale);
    }

    private void updateRedisClusterScaleToSuccess(final ClusterScale clusterScale) {
        clusterScale.setStatus(ScaleStatus.DONE);
        clusterScale.setResult(ScaleResult.SUCCESS);
        redisClusterScaleEntityService.updateById(clusterScale);
    }

    private void initMonitorAndConfig(final List<InstanceBO> scaledInstances) {
        monitorService.initMonitorForInstances(scaledInstances);
        redisConfigProcessService.effectConfigForScaledInstances(scaledInstances);
    }

    private void verticalScale(final Cluster presentCluster, final ClusterScale clusterScale) {
        Preconditions.checkArgument(presentCluster.getCacheSize() + clusterScale.getScaleNum() > 0,
                "New maxmemory must larger than 0.");

        // 检查可用容量是否足够
        List<InstanceBO> instanceBOS = instanceProcessService
                .queryInstanceBOsByClusterId(presentCluster.getClusterId());

        Map<Integer, Node> nodes = new HashMap<>();
        for (InstanceBO each : instanceBOS) {
            Node node = each.getNode();
            if (nodes.containsKey(node.getId())) {
                node = nodes.get(node.getId());
            } else {
                nodes.put(node.getId(), node);
            }

            int freeMemory = node.getFreeMemory();
            freeMemory -= clusterScale.getScaleNum();
            if (freeMemory < 0) {
                throw new NotEnoughResourcesException(
                        "We need '%d GB' free memory, but there are only '%d GB' in '%s'.", clusterScale.getScaleNum(),
                        each.getNode().getFreeMemory(), each.getNode().getIp());
            } else {
                node.setFreeMemory(freeMemory);
            }
        }
        nodeEntityService.updateBatchById(nodes.values());

        final long newSize = (presentCluster.getCacheSize() + clusterScale.getScaleNum()) * CapacityUnit.GB.getBytes();
        // update maxmemory for each redis server instance
        for (InstanceBO each : instanceBOS) {
            // set slot state to migrating in source node
            retryTemplate.execute(retryContext -> {
                redisClientUtilityService.configSet(each, RedisConfigItem.MAXMEMORY.getValue(), Long.toString(newSize));
                return Constant.Redis.EXECUTE_RESULT_SUCCESS;
            });
        }

        presentCluster.setCacheSize(presentCluster.getCacheSize() + clusterScale.getScaleNum());
        clusterEntityService.updateById(presentCluster);

        updateRedisClusterScaleToSuccess(clusterScale);
    }

    private List<InstanceBO> addSlaveToEveryMaster(final Cluster presentCluster, final ClusterScale clusterScale) {
        Cluster scaleCluster = createScaleRedisCluster(presentCluster);
        scaleCluster.setMasterCount(presentCluster.getMasterCount() * clusterScale.getScaleNum());
        scaleCluster.setReplicaCount(0);

        List<InstanceBO> scaledInstances = instanceProcessService.createInstances(scaleCluster);

        if (RedisMode.CLUSTER.equals(presentCluster.getRedisMode())) {
            this.addSlaveToEveryMasterForCluster(presentCluster, scaledInstances, clusterScale);
        } else {
            this.addSlaveToEveryMasterForStandalone(presentCluster, scaledInstances, clusterScale);
        }

        initMonitorAndConfig(scaledInstances);
        presentCluster.setReplicaCount(presentCluster.getReplicaCount() + clusterScale.getScaleNum());
        clusterEntityService.updateById(presentCluster);

        updateRedisClusterScaleToSuccess(clusterScale);
        return scaledInstances;
    }

    private void addSlaveToEveryMasterForCluster(final Cluster presentCluster,
            final List<InstanceBO> newServerInstances, final ClusterScale clusterScale) {
        List<InstanceBO> presentServerInstances = instanceProcessService
                .queryInstanceBOsByClusterId(clusterScale.getClusterId());
        assignReplication(presentServerInstances, newServerInstances);
        ClusterBO redisClusterBO = ClusterBO.builder().cluster(presentCluster).instanceBOs(newServerInstances).build();
        deploymentUtilityService.deployForScaleOfCluster(redisClusterBO, presentServerInstances.get(0));
        instanceEntityService
                .updateBatchById(newServerInstances.stream().map(InstanceBO::getSelf).collect(Collectors.toList()));
        instanceProcessService.queryAndUpdateRedisClusterNodeId(redisClusterBO);
    }

    private void addSlaveToEveryMasterForStandalone(final Cluster presentCluster,
            final List<InstanceBO> newServerInstances, final ClusterScale clusterScale) {
        List<InstanceBO> presentServerInstances = instanceProcessService
                .queryInstanceBOsByClusterId(clusterScale.getClusterId());
        assignReplication(presentServerInstances, newServerInstances);
        instanceEntityService
                .updateBatchById(newServerInstances.stream().map(InstanceBO::getSelf).collect(Collectors.toList()));
        ClusterBO redisClusterBO = ClusterBO.builder().cluster(presentCluster).instanceBOs(newServerInstances).build();
        deploymentUtilityService.deployForScaleOfStandalone(redisClusterBO);
    }

    private void assignReplication(final List<InstanceBO> presentInstances, final List<InstanceBO> newInstances) {
        int index = 0;
        for (int i = 0; i < newInstances.size(); i++) {
            InstanceBO newInstance = newInstances.get(i);
            while (index < presentInstances.size()) {
                InstanceBO presentInstance = presentInstances.get(index++);
                if (RedisReplicationRole.MASTER.equals(presentInstance.getSelf().getRole())) {
                    // if master and slave on same node, try to break it.
                    // only work when new instance size large than 1.
                    // TODO: when new instance size is 1, master and slave may in same node.
                    if (newInstance.getNode().equals(presentInstance.getNode()) && newInstances.size() > 1) {
                        if (i == newInstances.size() - 1) {
                            // change master with last instance.
                            InstanceBO firstNewInstance = newInstances.get(0);
                            relateMasterAndSlave(firstNewInstance.getMaster(), newInstance);
                            relateMasterAndSlave(presentInstance, firstNewInstance);
                        } else {
                            // change master with next instance.
                            newInstances.remove(i);
                            newInstances.add(i + 1, newInstance);
                            newInstance = newInstances.get(i);
                            relateMasterAndSlave(presentInstance, newInstance);
                        }
                    } else {
                        relateMasterAndSlave(presentInstance, newInstance);
                    }
                    break;
                }
            }
        }
    }

    private void relateMasterAndSlave(final InstanceBO master, final InstanceBO slave) {
        slave.setMaster(master);
        slave.getSelf().setSlaveof(String.format("%s:%d", master.getNode().getIp(), master.getSelf().getPort()));
        slave.getSelf().setRole(RedisReplicationRole.SLAVE);
    }

    private void rebalanceClusterAsync(final Cluster cluster, final ClusterScale clusterScale) {
        try {
            // rebalance slot
            processThreadPool.execute(() -> {
                // 开始移槽
                try {
                    rebalanceCluster(cluster);
                    // 扩容结果
                    clusterScale.setResult(ScaleResult.SUCCESS);
                } catch (Exception e) {
                    clusterScale.setResult(ScaleResult.FAIL);
                    setFailDescToScaleInfo(clusterScale, e);
                    log.error(String.format("Scale fail with id '%s'.", clusterScale.getId()), e);
                } finally {
                    clusterScale.setStatus(ScaleStatus.DONE);
                    redisClusterScaleEntityService.updateById(clusterScale);
                }
            });
        } catch (Exception e) {
            setFailDescToScaleInfo(clusterScale, e);
            clusterScale.setStatus(ScaleStatus.DONE);
            redisClusterScaleEntityService.updateById(clusterScale);
        }
    }

    private void rebalanceCluster(final Cluster cluster) {
        RedisClusterClient redisClusterClient = null;
        StatefulRedisClusterConnection redisConnection = null;

        try {
            final List<InstanceBO> instanceBOS = instanceProcessService
                    .queryInstanceBOsByClusterId(cluster.getClusterId());

            List<RedisURI> redisURIs = new ArrayList(instanceBOS.size());
            for (InstanceBO each : instanceBOS) {
                redisURIs.add(RedisURI.builder().withHost(each.getNode().getIp()).withPort(each.getSelf().getPort())
                        .build());
            }

            redisClusterClient = RedisClusterClient.create(redisURIs);
            redisClusterClient.setDefaultTimeout(Duration.ofSeconds(DEFAULT_TIMEOUT));
            redisConnection = redisClusterClient.connect();

            // assign slot
            List<MigratePlan> plans = getMigratePlans(redisConnection);
            migrateSlots(redisConnection, plans);
        } finally {
            if (redisConnection != null) {
                redisConnection.close();
            }

            if (redisClusterClient != null) {
                redisClusterClient.shutdown();
            }
        }
    }

    private List<MigratePlan> getMigratePlans(final StatefulRedisClusterConnection redisConnection) {
        final List<MigratePlan> migratePlans = new ArrayList<>();
        final RedisAdvancedClusterCommands commands = redisConnection.sync();

        // 获取所有的master节点
        NodeSelection masters = retryTemplate.execute((RetryCallback<NodeSelection, ScaleException>) retryContext -> {
            return commands.masters();
        });
        int slotCountAfterRebalance = SlotHash.SLOT_COUNT / masters.size();

        // 遍历master节点，找出需要移除槽的目标节点
        Map<Integer, RedisClusterNode> sourceSlotAndNodes = new TreeMap<>();
        for (int i = 0; i < masters.size(); i++) {
            RedisClusterNode eachMaster = masters.node(i);

            int slotDifference = eachMaster.getSlots().size() - slotCountAfterRebalance;
            if (slotDifference > BALANCE_THRESHOLD) {
                List<Integer> slotsInMaster = eachMaster.getSlots();
                for (int j = slotCountAfterRebalance; j < slotsInMaster.size(); j++) {
                    sourceSlotAndNodes.put(slotsInMaster.get(j), eachMaster);
                }
            } else if (slotDifference < -BALANCE_THRESHOLD) {
                MigratePlan migratePlan = new MigratePlan();
                migratePlan.setMigrateSlotsCount(-slotDifference);
                migratePlan.setSourceSlotAndNodes(new TreeMap<Integer, RedisClusterNode>());
                migratePlan.setTargetNode(eachMaster);

                migratePlans.add(migratePlan);
            }
        }

        // 分配槽移动方法
        int migratePlanIndex = 0;
        Iterator<Map.Entry<Integer, RedisClusterNode>> sourceSlotItera = sourceSlotAndNodes.entrySet().iterator();

        while (migratePlanIndex < migratePlans.size() && sourceSlotItera.hasNext()) {
            MigratePlan migratePlan = migratePlans.get(migratePlanIndex);
            int plannedSlot = 0;

            while (plannedSlot < migratePlan.getMigrateSlotsCount() && sourceSlotItera.hasNext()) {
                Map.Entry<Integer, RedisClusterNode> currentSlotAndNode = sourceSlotItera.next();
                migratePlan.getSourceSlotAndNodes().put(currentSlotAndNode.getKey(), currentSlotAndNode.getValue());
                plannedSlot++;
            }

            migratePlanIndex++;
        }

        return migratePlans;
    }

    private void migrateSlots(final StatefulRedisClusterConnection redisConnection,
            final List<MigratePlan> migratePlans) {
        for (MigratePlan each : migratePlans) {
            log.debug("Migrating '{}' slots to '{}'.", each.getMigrateSlotsCount(), each.getTargetNode().getUri());

            Iterator<Map.Entry<Integer, RedisClusterNode>> sourceSlotItera = each.getSourceSlotAndNodes().entrySet()
                    .iterator();
            while (sourceSlotItera.hasNext()) {
                Map.Entry<Integer, RedisClusterNode> eachSlotAndNode = sourceSlotItera.next();
                RedisClusterNode targetNode = each.getTargetNode();
                RedisClusterNode sourceNode = eachSlotAndNode.getValue();
                int slot = eachSlotAndNode.getKey();

                migrateSlot(redisConnection, sourceNode, targetNode, slot);
            }
        }
    }

    private void migrateSlot(final StatefulRedisClusterConnection redisConnection, final RedisClusterNode sourceNode,
            final RedisClusterNode targetNode, final int slot) {
        final RedisAdvancedClusterCommands commands = redisConnection.sync();

        // set slot state to migrating in source node
        retryTemplate.execute(retryContext -> {
            String result = commands.getConnection(sourceNode.getNodeId())
                    .clusterSetSlotMigrating(slot, targetNode.getNodeId());
            if (Constant.Redis.EXECUTE_RESULT_SUCCESS.equals(result)) {
                return result;
            }

            throw new ScaleException("Fail to set state to migrating of slot '%d', return value is '%s'.", slot,
                    result);
        });

        // set slot state to importing in target node
        retryTemplate.execute(retryContext -> {
            String result = commands.getConnection(targetNode.getNodeId())
                    .clusterSetSlotImporting(slot, sourceNode.getNodeId());
            if (Constant.Redis.EXECUTE_RESULT_SUCCESS.equals(result)) {
                return result;
            }

            throw new ScaleException("Fail to set state to importing of slot '%d', return value is '%s'.", slot,
                    result);
        }, retryContext -> {
            // recovery slot state to stable
            commands.getConnection(sourceNode.getNodeId()).clusterSetSlotStable(slot);

            Throwable lastThrowable = retryContext.getLastThrowable();
            if (retryContext.getLastThrowable() instanceof ScaleException) {
                throw (ScaleException) lastThrowable;
            } else {
                throw new ScaleException(lastThrowable);
            }
        });

        try {
            migrateKeysInSlot(redisConnection, sourceNode, targetNode, slot);
        } catch (ScaleException e) {
            commands.getConnection(sourceNode.getNodeId()).clusterSetSlotStable(slot);
            commands.getConnection(targetNode.getNodeId()).clusterSetSlotStable(slot);
            throw e;
        }

        setSlotToTargetNode(redisConnection, targetNode, slot);
    }

    private void migrateKeysInSlot(final StatefulRedisClusterConnection redisConnection,
            final RedisClusterNode sourceNode, final RedisClusterNode targetNode, final int slot) {
        RedisAdvancedClusterCommands commands = redisConnection.sync();
        retryTemplate.execute(retryContext -> {
            boolean migrateDone = false;
            while (!migrateDone) {
                List<String> keys = commands.getConnection(sourceNode.getNodeId())
                        .clusterGetKeysInSlot(slot, KEYS_PER_MIGRATE);
                log.debug("Migrating '{}' keys to '{}'.", keys.size(), targetNode.getUri());

                if (keys.isEmpty()) {
                    migrateDone = true;
                } else {
                    String[] keysArray = keys.toArray(new String[keys.size()]);

                    // do migrate
                    String result = commands.migrate(targetNode.getUri().getHost(), targetNode.getUri().getPort(), 0,
                            MIGRATE_TIMEOUT * (retryContext.getRetryCount() + 1),
                            MigrateArgs.Builder.keys(keysArray).copy().replace());

                    // if result is OK, del keys from source node
                    switch (result) {
                        case Constant.Redis.EXECUTE_RESULT_SUCCESS:
                            commands.del(keysArray);
                            if (keys.size() < KEYS_PER_MIGRATE) {
                                migrateDone = true;
                            }
                            break;
                        case Constant.Redis.EXECUTE_RESULT_NOKEY:
                            migrateDone = true;
                            break;
                        default:
                            throw new ScaleException(String.format("Migrate fail of slot '%d'.", slot));
                    }
                }
            }

            return Constant.Redis.EXECUTE_RESULT_SUCCESS;
        });
    }

    private void setSlotToTargetNode(final StatefulRedisClusterConnection redisConnection,
            final RedisClusterNode targetNode, final int slot) {
        RedisAdvancedClusterCommands commands = redisConnection.sync();
        retryTemplate.execute(retryContext -> {
            NodeSelection masters = commands.masters();
            int succeedMasterCount = 0;
            StringBuffer failMessage = new StringBuffer();
            for (int i = 0; i < masters.size(); i++) {
                String result = commands.getConnection(masters.node(i).getNodeId()).
                        clusterSetSlotNode(slot, targetNode.getNodeId());
                if (Constant.Redis.EXECUTE_RESULT_SUCCESS.equals(result)) {
                    succeedMasterCount++;
                } else {
                    failMessage.append(masters.node(i).getUri());
                    failMessage.append(": ");
                    failMessage.append(result);
                    failMessage.append("\n");
                }
            }
            if (succeedMasterCount * 2 < masters.size()) {
                throw new ScaleException("Fail to set node of slot '%d', return value is '%s'.", slot,
                        failMessage.toString());
            }
            return Constant.Redis.EXECUTE_RESULT_SUCCESS;
        });
    }

    private void setFailDescToScaleInfo(final ClusterScale clusterScale, final Throwable throwable) {
        String resultDesc = throwable.getMessage();
        if (resultDesc.length() > MAX_RESULT_DESC_LENGTH) {
            resultDesc = resultDesc.substring(0, MAX_RESULT_DESC_LENGTH);
        }
        clusterScale.setResultDesc(resultDesc);
    }

    @Override
    @Transactional
    public void retryLastScale(final Integer scaleId) {
        ClusterScale clusterScale = redisClusterScaleEntityService.getById(scaleId);
        // 目前只支持重试集群模式的最后一次水平扩容
        switch (clusterScale.getType()) {
            case HORIZONTAL:
                Cluster cluster = clusterEntityService.getByClusterId(clusterScale.getClusterId());
                if (RedisMode.CLUSTER.equals(cluster.getRedisMode())) {
                    // 确认该扩容是最后一次水平扩容
                    List<ClusterScale> clusterScales = redisClusterScaleEntityService
                            .queryByClusterId(clusterScale.getClusterId());
                    for (ClusterScale each : clusterScales) {
                        if (ScaleType.HORIZONTAL.equals(each.getType())) {
                            if (each.getId().equals(clusterScale.getId())) {
                                // 修改状态为进行中
                                clusterScale.setStatus(ScaleStatus.DOING);
                                clusterScale.setResult(null);
                                clusterScale.setResultDesc(null);
                                redisClusterScaleEntityService.updateById(clusterScale);

                                rebalanceClusterAsync(cluster, clusterScale);
                                return;
                            }
                            break;
                        }
                    }
                }
                break;
            default:
                break;
        }

        throw new ScaleException("You can just retry the last time horizontal scale for cluster mode.");
    }

    @Getter
    @Setter
    class MigratePlan {

        private RedisClusterNode targetNode;

        private int migrateSlotsCount;

        private Map<Integer, RedisClusterNode> sourceSlotAndNodes;
    }
}
