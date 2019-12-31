package com.dangdang.cymbal.web.controller;

import com.dangdang.cymbal.common.spi.UserProcessService;
import com.dangdang.cymbal.service.auth.service.process.UserRoleProcessService;
import com.dangdang.cymbal.service.monitor.enums.MonitorType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Principal;

/**
 * Controller for index page.
 *
 * @auther GeZhen
 */
@Controller
public class IndexController extends BaseController {

    // TODO: Better way.
    @Value("${monitor.type}")
    private MonitorType monitorType;

    @Resource
    private UserProcessService userProcessService;

    @Resource
    private UserRoleProcessService userRoleProcessService;

    @GetMapping("/")
    public String index(@AuthenticationPrincipal Principal principal, Model model, HttpSession httpSession) {
        String userName = principal.getName();
        model.addAttribute("userName", userName);
        model.addAttribute("userCnName", userProcessService.getUserCnName(userName));
        httpSession.setAttribute("isAdmin", userRoleProcessService.isAdmin(userName));
        httpSession.setAttribute("defaultMonitor", monitorType == MonitorType.DEFAULT);
        return "index";
    }

    @GetMapping(value = "/page")
    public ModelAndView page(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("redirect:/");
    }
}
