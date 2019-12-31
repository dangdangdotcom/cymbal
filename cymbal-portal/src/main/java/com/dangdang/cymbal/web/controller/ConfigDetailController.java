package com.dangdang.cymbal.web.controller;

import com.dangdang.cymbal.domain.bo.ConfigDetailBO;
import com.dangdang.cymbal.domain.po.ConfigDetail;
import com.dangdang.cymbal.service.operation.service.entity.ConfigDetailEntityService;
import com.dangdang.cymbal.service.operation.service.process.ConfigDetailProcessService;
import com.dangdang.cymbal.web.object.converter.ConfigDetailConverter;
import com.dangdang.cymbal.web.object.dto.ConfigDetailDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * Redis config detail controller.
 *
 * @auther GeZhen
 */
@Controller
public class ConfigDetailController extends BaseController {

    @Resource
    private ConfigDetailConverter configDetailConverter;

    @Resource
    private ConfigDetailEntityService redisConfigDetailEntityService;

    @Resource
    private ConfigDetailProcessService redisConfigDetailProcessService;

    /**
     * Query redis config details by redis config id.
     *
     * @param configId redis config id
     * @return redis config detail DTOs
     */
    @GetMapping("/configs/{configId}/details")
    @ResponseBody
    public List<ConfigDetailDTO> queryByConfigId(final @PathVariable Integer configId) {
        List<ConfigDetailBO> configDetailBOs = redisConfigDetailProcessService.queryByConfigId(configId);
        return configDetailConverter.bosToDtos(configDetailBOs);
    }

    /**
     * Create or update a redis config detail with config name.
     *
     * @param configDetailDTO config detail DTO
     */
    @PutMapping("/configs/{configId}/details")
    @ResponseBody
    public void createOrUpdateRedisConfigDetail(final @RequestBody ConfigDetailDTO configDetailDTO) {
        ConfigDetail configDetail = configDetailConverter.dtoToPo(configDetailDTO);
        redisConfigDetailProcessService.createOrUpdateConfigDetail(configDetail);
    }

    /**
     * Effect config detail to redis server.
     *
     * @param configId redis config id
     */
    @PatchMapping("/configs/{configId}/details")
    @ResponseBody
    public void effectRedisConfig(final @PathVariable Integer configId) {
        redisConfigDetailProcessService.effectConfigDetails(configId);
    }
}
