package com.dangdang.cymbal.web.controller;

import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.Cluster;
import com.dangdang.cymbal.domain.po.Node;
import com.dangdang.cymbal.domain.po.RedisMode;
import com.dangdang.cymbal.service.cluster.service.entity.ClusterEntityService;
import com.dangdang.cymbal.service.cluster.service.process.InstanceProcessService;
import com.dangdang.cymbal.service.node.service.entity.NodeEntityService;
import com.dangdang.cymbal.service.operation.service.utility.RedisReplicationUtilityService;
import com.dangdang.cymbal.web.object.converter.ClusterConverter;
import com.dangdang.cymbal.web.object.converter.InstanceConverter;
import com.dangdang.cymbal.web.object.converter.NodeConverter;
import com.dangdang.cymbal.web.object.dto.InstanceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

/**
 * Controller for redis server instance.
 *
 * @author gezhen
 */
@Slf4j
@Controller
public class InstanceController extends BaseController {

    @Resource
    private NodeEntityService nodeEntityService;

    @Resource
    private ClusterEntityService clusterEntityService;

    @Resource
    private InstanceProcessService instanceProcessService;

    @Resource
    private RedisReplicationUtilityService redisReplicationUtilityService;

    @Resource
    private InstanceConverter redisServerInstanceConverter;

    @Resource
    private NodeConverter nodeConverter;

    @Resource
    private ClusterConverter clusterConverter;

    @GetMapping("/clusters/{clusterId}/instances/page")
    @PreAuthorize(value = "@clusterPermissionChecker.hasViewPermissionForCluster(#clusterId, principal.username)")
    public ModelAndView pageOfCluster(final @PathVariable String clusterId) {
        ModelAndView modelAndView = new ModelAndView("redis/nav/instances_nav");
        boolean isRedisCluster = false;
        Cluster cluster = clusterEntityService.getByClusterId(clusterId);
        if (RedisMode.CLUSTER.equals(cluster.getRedisMode())) {
            isRedisCluster = true;
        }
        modelAndView.addObject("cluster", clusterConverter.poToDto(cluster));
        return modelAndView;
    }

    @GetMapping("/nodes/{nodeId}/instances/page")
    public ModelAndView pageOfNode(@PathVariable String nodeId) {
        ModelAndView modelAndView = new ModelAndView("redis/nav/instances_nav");
        Node node = nodeEntityService.getById(nodeId);
        modelAndView.addObject("node", nodeConverter.poToDto(node));
        return modelAndView;
    }

    @GetMapping(value = "/clusters/{clusterId}/instances")
    @PreAuthorize(value = "@clusterPermissionChecker.hasViewPermissionForCluster(#clusterId, principal.username)")
    @ResponseBody
    public List<InstanceDTO> queryByClusterId(@PathVariable String clusterId) {
        List<InstanceBO> instanceBOS = instanceProcessService.queryInstanceBOsByClusterId(clusterId);
        return redisServerInstanceConverter.bosToDtos(instanceBOS);
    }

    @GetMapping(value = "/nodes/{nodeId}/instances")
    @ResponseBody
    public List<InstanceDTO> queryByNodeId(@PathVariable Integer nodeId) {
        List<InstanceBO> instanceBOS = instanceProcessService.queryInstanceBOsByNodeId(nodeId);
        return redisServerInstanceConverter.bosToDtos(instanceBOS);
    }

    @PatchMapping(value = "/clusters/{clusterId}/instances")
    @PreAuthorize(value = "@clusterPermissionChecker.hasViewPermissionForCluster(#clusterId, principal.username)")
    @ResponseBody
    public ResponseEntity<String> refreshReplication(@PathVariable String clusterId) {
        if (redisReplicationUtilityService.refreshReplication(clusterId)) {
            return ResponseEntity.status(HttpStatus.RESET_CONTENT).build();
        }
        return ResponseEntity.ok().build();
    }
}
