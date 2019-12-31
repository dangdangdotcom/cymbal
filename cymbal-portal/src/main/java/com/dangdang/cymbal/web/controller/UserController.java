package com.dangdang.cymbal.web.controller;

import com.dangdang.cymbal.common.spi.UserProcessService;
import com.dangdang.cymbal.domain.po.User;
import com.dangdang.cymbal.service.auth.service.entity.UserEntityService;
import com.dangdang.cymbal.web.object.converter.UserConverter;
import com.dangdang.cymbal.web.object.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

/**
 * Controller for {@link User}.
 *
 * @auther GeZhen
 */
@Controller
@Slf4j
public class UserController extends BaseController {

    @Resource
    private UserProcessService userProcessService;

    @Resource
    private UserEntityService userEntityService;

    @Resource
    private UserConverter userConverter;

    @GetMapping("/users/page")
    public ModelAndView usersPage() {
        return new ModelAndView("/user/nav/users_nav");
    }

    @GetMapping("/users")
    @ResponseBody
    public List<UserDTO> queryAllUsers() {
        List<User> users = userProcessService.queryAllUsers();
        return userConverter.posToDtos(users);
    }

    @PostMapping("/users")
    @ResponseBody
    public ResponseEntity<String> createUser(@RequestBody UserDTO userDTO) {
        User user = userConverter.dtoToPo(userDTO);
        try {
            return ResponseEntity.ok(userProcessService.createUser(user).toString());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(String.format("User with name '%s' already exists.", user.getUserName()));
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }
    }

    @DeleteMapping("/users/{userId}")
    @ResponseBody
    public void deleteUser(@PathVariable Integer userId) {
        userEntityService.removeById(userId);
    }

    @PutMapping("/users/{userId}")
    @ResponseBody
    public void updateUser(@RequestBody UserDTO userDTO) {
        User user = userConverter.dtoToPo(userDTO);
        userEntityService.updateById(user);
    }
}
