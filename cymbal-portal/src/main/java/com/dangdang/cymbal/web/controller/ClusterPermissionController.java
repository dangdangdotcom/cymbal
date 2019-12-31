package com.dangdang.cymbal.web.controller;

import com.dangdang.cymbal.domain.po.ClusterPermission;
import com.dangdang.cymbal.service.auth.service.entity.ClusterPermissionEntityService;
import com.dangdang.cymbal.web.object.converter.ClusterPermissionConverter;
import com.dangdang.cymbal.web.object.dto.ClusterPermissionDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * Redis cluster permission controller.
 *
 * @auther GeZhen
 */
@Controller
public class ClusterPermissionController extends BaseController {

    @Resource
    private ClusterPermissionConverter clusterPermissionConverter;

    @Resource
    private ClusterPermissionEntityService clusterPermissionEntityService;

    /**
     * Query redis cluster permission by cluster id.
     *
     * @param clusterId cluster id
     * @return redis cluster permission DTOs
     */
    @GetMapping("/clusters/{clusterId}/permissions")
    @ResponseBody
    public List<ClusterPermissionDTO> queryClusterPermissionByClusterId(final @PathVariable String clusterId) {
        List<ClusterPermission> clusterPermissions = clusterPermissionEntityService.queryByClusterId(clusterId);
        return clusterPermissionConverter.posToDtos(clusterPermissions);
    }

    /**
     * Create a permission for redis cluster.
     *
     * @param clusterId cluster id
     * @param clusterPermissionDTO cluster permission DTO
     */
    @PostMapping("/clusters/{clusterId}/permissions")
    @PreAuthorize(value = "@clusterPermissionChecker.hasOperationPermissionForCluster(#clusterId, principal.username)")
    @ResponseBody
    public void createClusterPermission(final @PathVariable String clusterId,
            final @RequestBody ClusterPermissionDTO clusterPermissionDTO) {
        ClusterPermission clusterPermission = clusterPermissionConverter.dtoToPo(clusterPermissionDTO);
        clusterPermissionEntityService.save(clusterPermission);
    }

    /**
     * Delete a redis cluster permission by permission id.
     *
     * @param clusterId cluster id
     * @param permissionId redis cluster permission id
     */
    @DeleteMapping("/clusters/{clusterId}/permissions/{permissionId}")
    @PreAuthorize(value = "@clusterPermissionChecker.hasOperationPermissionForCluster(#clusterId, principal.username)")
    @ResponseBody
    public void deleteClusterPermission(final @PathVariable String clusterId,
            final @PathVariable Integer permissionId) {
        clusterPermissionEntityService.removeById(permissionId);
    }

    /**
     * Delete a redis cluster permissions by permission ids.
     *
     * @param clusterId cluster id
     * @param permissionIds redis cluster permission ids
     */
    @DeleteMapping("/clusters/{clusterId}/permissions")
    @PreAuthorize(value = "@clusterPermissionChecker.hasOperationPermissionForCluster(#clusterId, principal.username)")
    @ResponseBody
    public void deleteClusterPermissions(final @PathVariable String clusterId,
            final @RequestBody List<Integer> permissionIds) {
        clusterPermissionEntityService.removeByIds(permissionIds);
    }
}
