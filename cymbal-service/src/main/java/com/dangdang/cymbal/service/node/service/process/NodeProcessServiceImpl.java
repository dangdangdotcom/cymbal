package com.dangdang.cymbal.service.node.service.process;

import com.dangdang.cymbal.domain.po.Environment;
import com.dangdang.cymbal.domain.po.InternetDataCenter;
import com.dangdang.cymbal.domain.po.Node;
import com.dangdang.cymbal.domain.po.NodeStatus;
import com.dangdang.cymbal.service.constant.Constant;
import com.dangdang.cymbal.service.monitor.service.MonitorService;
import com.dangdang.cymbal.service.node.exception.NotEnoughResourcesException;
import com.dangdang.cymbal.service.node.exception.ParseExcelFileException;
import com.dangdang.cymbal.service.node.service.entity.NodeEntityService;
import com.dangdang.cymbal.service.util.enums.AnsiblePlayBookName;
import com.dangdang.cymbal.service.util.service.AnsibleService;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implement of {@link NodeProcessService}.
 *
 * @auther GeZhen
 */
@Slf4j
@Service
public class NodeProcessServiceImpl implements NodeProcessService {

    private static final String EXCEL_SUFFIX = "xlsx";

    private static final int MIN_NODE_IN_CLUSTER = 3;

    @Resource
    private NodeEntityService nodeEntityService;

    @Resource
    private AnsibleService ansibleService;

    @Resource
    private MonitorService monitorService;

    @Override
    @Transactional
    public int createNodesFromExcel(final String filePath) {
        checkFile(filePath);
        List<Node> nodes = getNodesFromExcelFile(filePath);
        removeExistedNodes(nodes);
        nodeEntityService.saveBatch(nodes);
        return nodes.size();
    }

    private void checkFile(final String filePath) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(filePath), "File path can not be null or empty.");
        Preconditions.checkArgument(filePath.endsWith(EXCEL_SUFFIX), "File postfix must be '%s'.", EXCEL_SUFFIX);
        Preconditions.checkArgument(new File(filePath).exists(), "File of path '%s' is not exist.", EXCEL_SUFFIX);
    }

    private List<Node> getNodesFromExcelFile(final String excelFilePath) {
        List<Node> nodes = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(excelFilePath))) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row currentRow = sheet.getRow(rowNum);
                if (Objects.isNull(currentRow.getCell(0)) || Strings
                        .isNullOrEmpty(currentRow.getCell(0).getStringCellValue())) {
                    break;
                }
                nodes.add(getNodeFromExcelRow(currentRow));
            }

            if (nodes.isEmpty()) {
                throw new ParseExcelFileException("No node info in uploaded excel file.");
            }
            return nodes;
        } catch (final IOException | IllegalArgumentException e) {
            throw new ParseExcelFileException(e);
        }
    }

    private Node getNodeFromExcelRow(final Row row) {
        Node node = new Node();
        node.setIp(row.getCell(0).getStringCellValue().trim().replaceAll("\\p{C}", ""));
        node.setHost(row.getCell(1).getStringCellValue());
        node.setTotalMemory((int) row.getCell(2).getNumericCellValue());
        node.setFreeMemory(node.getTotalMemory());
        node.setIdc(InternetDataCenter.valueOf(row.getCell(3).getStringCellValue().toUpperCase()));
        node.setEnv(Environment.valueOf(row.getCell(4).getStringCellValue().toUpperCase()));
        if (Objects.isNull(row.getCell(5))) {
            node.setPassword(Constant.Strings.EMPTY);
        } else if (Cell.CELL_TYPE_NUMERIC == row.getCell(5).getCellType()) {
            node.setPassword(String.valueOf(row.getCell(5).getNumericCellValue()));
        } else {
            node.setPassword(row.getCell(5).getStringCellValue());
        }
        node.setStatus(NodeStatus.UNINITIALIZED);
        node.setCreationDate(new Date());
        node.setLastChangedDate(node.getCreationDate());
        return node;
    }

    private void removeExistedNodes(final List<Node> nodes) {
        Iterator<Node> iterator = nodes.iterator();
        while (iterator.hasNext()) {
            if (isExisted(iterator.next())) {
                iterator.remove();
            }
        }
    }

    private boolean isExisted(final Node node) {
        return !Objects.isNull(nodeEntityService.getByIp(node.getIp()));
    }

    @Override
    public void saveAndInitNode(final Node node) {
        Preconditions.checkNotNull(node);
        addOrGetProperties(node);
        initNodes(Arrays.asList(node));
    }

    private void addOrGetProperties(final Node node) {
        Node existedNode = nodeEntityService.getByIp(node.getIp());
        if (Objects.isNull(existedNode)) {
            nodeEntityService.save(node);
        } else {
            BeanUtils.copyProperties(existedNode, node);
        }
    }

    @Override
    @Transactional
    public List<Node> initNodes(Set<Integer> nodeIds) {
        List<Node> nodes = new ArrayList<>();
        for (Integer nodeId : nodeIds) {
            Node node = nodeEntityService.getById(nodeId);
            Preconditions.checkNotNull(node);
            nodes.add(node);
        }
        return initNodes(nodes);
    }

    @Override
    @Transactional
    public List<Node> initNodes(final List<Node> nodes) {
        List<Node> nodesToInit = getUninitializedNodes(nodes);
        try {
            List<Node> succeedNodes = initEnvironmentForNodes(nodesToInit);
            monitorService.initMonitorForNodes(succeedNodes);
            updateNodesToInitialized(succeedNodes);
            nodesToInit.removeAll(succeedNodes);
        } finally {
            return nodesToInit;
        }
    }

    private List<Node> getUninitializedNodes(final List<Node> nodes) {
        return nodes.stream().filter(node -> NodeStatus.UNINITIALIZED.equals(node.getStatus()))
                .collect(Collectors.toList());
    }

    private List<Node> initEnvironmentForNodes(final List<Node> nodes) {
        return ansibleService.runPlayBookOnNodes(AnsiblePlayBookName.TOTAL_INIT, nodes);
    }

    private void updateNodesToInitialized(final List<Node> nodes) {
        Preconditions.checkArgument(!nodes.isEmpty());
        nodes.stream().forEach(node -> node.setStatus(NodeStatus.INITIALIZED));
        nodeEntityService.updateBatchById(nodes);
    }

    @Override
    public List<Node> queryAvailableNodes(final InternetDataCenter idc, final int cacheSize, final int masterCount,
            final int replicaCount) {
        List<Node> nodes = queryNodesByTargetIdcAndMinFreeMemory(idc, cacheSize);
        checkResourcesIsEnough(nodes, cacheSize, masterCount, replicaCount);
        return nodes;
    }

    private List<Node> queryNodesByTargetIdcAndMinFreeMemory(final InternetDataCenter dataCenter,
            final int minFreeMemory) {
        return nodeEntityService.lambdaQuery().eq(Node::getStatus, NodeStatus.INITIALIZED).eq(Node::getIdc, dataCenter)
                .ge(Node::getFreeMemory, minFreeMemory).list();
    }

    private void checkResourcesIsEnough(final List<Node> nodes, final int cacheSize, final int masterCount,
            final int replicaCount) {
        if (masterCount >= MIN_NODE_IN_CLUSTER && nodes.size() < MIN_NODE_IN_CLUSTER) {
            throw new NotEnoughResourcesException("We need at least '%d' nodes, but there are only '%d'.",
                    MIN_NODE_IN_CLUSTER, nodes.size());
        }
        // TODO: We should check resources about cpu, net i/o, disk, memory and so on, but not memory only.
        int freeMemorySum = nodes.stream().mapToInt(node -> node.getFreeMemory() - node.getFreeMemory() % cacheSize)
                .sum();
        int needMemorySum = calculateMemorySum(cacheSize, masterCount, replicaCount);
        if (freeMemorySum < needMemorySum) {
            throw new NotEnoughResourcesException("We need '%d GB' free memory, but there are only '%d GB'.",
                    needMemorySum, freeMemorySum);
        }
    }

    private int calculateMemorySum(final int cacheSize, final int masterCount, final int replicaCount) {
        return cacheSize * (masterCount * (replicaCount + 1));
    }

    @Override
    public void updateNode(final Node node) {
        Node oldNode = getOldNode(node.getId());
        updateNode(oldNode, node);
    }

    private Node getOldNode(final Integer nodeId) {
        Node oldNode = nodeEntityService.getById(nodeId);
        Preconditions.checkNotNull(oldNode);
        return oldNode;
    }

    private void updateNode(final Node oldNode, final Node newNode) {
        newNode.setCreationDate(oldNode.getCreationDate());
        nodeEntityService.updateById(newNode);
    }
}
