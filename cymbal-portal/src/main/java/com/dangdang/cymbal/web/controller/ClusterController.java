package com.dangdang.cymbal.web.controller;

import com.dangdang.cymbal.common.spi.UserProcessService;
import com.dangdang.cymbal.domain.bo.ClusterBO;
import com.dangdang.cymbal.service.cluster.service.process.ClusterProcessService;
import com.dangdang.cymbal.web.object.converter.ClusterConverter;
import com.dangdang.cymbal.web.object.dto.ClusterDTO;
import com.dangdang.cymbal.web.object.dto.ImportClusterDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

/**
 * Controller for redis cluster.
 *
 * @author GeZhen
 */
@Controller
public class ClusterController extends BaseController {

    @Resource
    private ClusterProcessService clusterProcessService;

    @Resource
    private UserProcessService userProcessService;

    @Resource
    private ClusterConverter clusterConverter;

    @GetMapping("/clusters/page")
    public ModelAndView page() {
        return new ModelAndView("redis/nav/clusters_nav");
    }

    @GetMapping("/user/clusters")
    @ResponseBody
    public List<ClusterDTO> queryByUserName(final @AuthenticationPrincipal Principal principal) {
        return clusterConverter.posToDtos(clusterProcessService.queryByUserName(principal.getName()));
    }

    @PostMapping(value = "/import/clusters")
    @ResponseBody
    public String importCluster(final @RequestBody ImportClusterDTO importClusterDTO,
            final @AuthenticationPrincipal Principal principal) {
        setUserNameIfNeeded(importClusterDTO, principal);
        ClusterBO clusterBO = clusterConverter.dtoToBo(importClusterDTO);
        clusterProcessService.createRedisClusterByImport(clusterBO);
        return clusterBO.getCluster().getClusterId();
    }

    private void setUserNameIfNeeded(final @RequestBody ImportClusterDTO importClusterDTO,
            final @AuthenticationPrincipal Principal principal) {
        if (Objects.isNull(importClusterDTO.getUserName())) {
            importClusterDTO.setUserName(principal.getName());
            importClusterDTO.setUserCnName(userProcessService.getUserCnName(principal.getName()));
        }
    }
}
