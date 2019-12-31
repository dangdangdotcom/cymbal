package com.dangdang.cymbal.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for auth.
 *
 * @auther GeZhen
 */
@Controller
public class AuthController {

    @GetMapping(value = "/logout")
    public String logout() {
        return "login";
    }

    @GetMapping(value = "/login")
    public String login() {
        return "login";
    }
}
