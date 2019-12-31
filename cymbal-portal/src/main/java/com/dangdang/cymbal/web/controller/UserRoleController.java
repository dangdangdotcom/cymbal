package com.dangdang.cymbal.web.controller;

import com.dangdang.cymbal.domain.po.UserRole;
import com.dangdang.cymbal.service.auth.service.entity.UserRoleEntityService;
import com.dangdang.cymbal.service.auth.service.process.UserRoleProcessService;
import com.dangdang.cymbal.web.object.converter.UserRoleConverter;
import com.dangdang.cymbal.web.object.dto.UserRoleDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * @auther GeZhen
 */
@Controller
@Slf4j
public class UserRoleController {

    @Resource
    private UserRoleProcessService userRoleProcessService;

    @Resource
    private UserRoleEntityService userRoleEntityService;

    @Resource
    private UserRoleConverter userRoleConverter;

    @GetMapping("/users/roles/page")
    public ModelAndView userRolesPage() {
        return new ModelAndView("/user/nav/user_roles_nav");
    }

    @GetMapping("/users/roles")
    @ResponseBody
    public List<UserRoleDTO> queryAllUserRoles() {
        List<UserRole> userRoles = userRoleEntityService.list();
        return userRoleConverter.posToDtos(userRoles);
    }

    @PostMapping("/users/roles")
    @ResponseBody
    public Integer createUserRole(@RequestBody UserRoleDTO userRoleDTO) {
        UserRole userRole = userRoleConverter.dtoToPo(userRoleDTO);
        return userRoleProcessService.createUserRole(userRole);
    }
}
