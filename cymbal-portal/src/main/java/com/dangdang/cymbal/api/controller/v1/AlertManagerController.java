package com.dangdang.cymbal.api.controller.v1;

import com.dangdang.cymbal.service.monitor.service.AlertManagerService;
import com.dangdang.cymbal.web.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Open api for alert message.
 * Such as alert push from prometheus and alert manager.
 *
 * @author GeZhen
 */
@Slf4j
@Controller
@RequestMapping("/api/v1/alertmanager")
public class AlertManagerController extends BaseController {

    @Resource
    private AlertManagerService alertManagerService;

    @ResponseBody
    @RequestMapping(value = "/webhook", method = RequestMethod.POST)
    public void prometheusAlertManagerWebhook(@RequestBody Object alertInfo) {
        log.debug("Alertmanager alert info {}.", alertInfo);
        alertManagerService.handleAlert(alertInfo);
    }
}
