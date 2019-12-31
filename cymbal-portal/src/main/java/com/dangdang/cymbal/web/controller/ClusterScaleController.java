package com.dangdang.cymbal.web.controller;

import com.dangdang.cymbal.domain.po.ClusterScale;
import com.dangdang.cymbal.service.node.exception.NotEnoughResourcesException;
import com.dangdang.cymbal.service.operation.service.entity.ClusterScaleEntityService;
import com.dangdang.cymbal.service.operation.service.process.ClusterScaleProcessService;
import com.dangdang.cymbal.web.object.converter.ClusterScaleConverter;
import com.dangdang.cymbal.web.object.dto.ClusterScaleDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.List;

/**
 * Controller for cluster scale.
 *
 * @auther GeZhen
 */
@Controller
@Slf4j
public class ClusterScaleController extends BaseController {

    @Resource
    private ClusterScaleConverter clusterScaleConverter;

    @Resource
    private ClusterScaleProcessService redisClusterScaleProcessService;

    @Resource
    private ClusterScaleEntityService redisClusterScaleEntityService;

    /**
     * Query cluster scales by cluster id.
     *
     * @param clusterId cluster id
     * @return redis cluster scale DTOs
     */
    @GetMapping("/clusters/{clusterId}/scales")
    @ResponseBody
    public List<ClusterScaleDTO> queryClusterScaleByClusterId(final @PathVariable String clusterId) {
        List<ClusterScale> clusterScales = redisClusterScaleEntityService.queryByClusterId(clusterId);
        return clusterScaleConverter.posToDtos(clusterScales);
    }

    /**
     * Create and do cluster scale.
     *
     * @param clusterId cluster id
     * @param clusterScaleDTO cluster scale DTO
     * @return http response entity
     */
    @PostMapping("/clusters/{clusterId}/scales")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<String> doScale(final @PathVariable String clusterId,
            final @RequestBody ClusterScaleDTO clusterScaleDTO, final @AuthenticationPrincipal Principal principal) {
        ClusterScale clusterScale = clusterScaleConverter.dtoToPo(clusterScaleDTO);
        clusterScale.setOperator(principal.getName());
        try {
            redisClusterScaleProcessService.doScale(clusterScale);
            return ResponseEntity.ok().build();
        } catch (NotEnoughResourcesException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retry a failed scale.
     * The scale must be last and fail one.
     *
     * @param clusterId cluster id
     * @param scaleId scale id
     * @return http response entity
     */
    @PatchMapping("/clusters/{clusterId}/scales/{scaleId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<String> retryLastScale(final @PathVariable String clusterId,
            final @PathVariable Integer scaleId) {
        try {
            redisClusterScaleProcessService.retryLastScale(scaleId);
            return ResponseEntity.ok().build();
        } catch (SecurityException e) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }
}
