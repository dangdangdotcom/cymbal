package com.dangdang.cymbal.web.controller;

import com.dangdang.cymbal.domain.po.Config;
import com.dangdang.cymbal.service.operation.service.process.ConfigProcessService;
import com.dangdang.cymbal.web.object.converter.ConfigConverter;
import com.dangdang.cymbal.web.object.dto.ConfigDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.List;


/**
 * Redis config controller.
 *
 * @author GeZhen
 */
@Controller
public class ConfigController {

    @Resource
    private ConfigConverter configConverter;

    @Resource
    private ConfigProcessService redisConfigProcessService;

    /**
     * Query configs by user name.
     * If user is system admin, method will return all config DTOs.
     *
     * @param principal auth principal
     * @return all config DTOs of granted permission cluster
     */
    @GetMapping("/user/configs")
    @ResponseBody
    public List<ConfigDTO> queryByUserName(@AuthenticationPrincipal final Principal principal) {
        List<Config> configs = redisConfigProcessService.queryByUserName(principal.getName());
        return configConverter.posToDtos(configs);
    }

    /**
     * Update name of redis config.
     *
     * @param clusterId cluster id
     * @param configId config id
     * @param configName new config name
     */
    @PatchMapping("/clusters/{clusterId}/configs/{configId}")
    @PreAuthorize(value = "@clusterPermissionChecker.hasOperationPermissionForCluster(#clusterId, principal.username)")
    @ResponseBody
    public void updateConfigName(@PathVariable final String clusterId, @PathVariable final Integer configId,
            final @RequestBody String configName) {
        redisConfigProcessService.updateConfigName(configId, configName);
    }
}
