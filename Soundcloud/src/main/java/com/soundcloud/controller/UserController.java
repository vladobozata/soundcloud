package com.soundcloud.controller;

import com.soundcloud.model.DTOs.*;
import com.soundcloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class UserController extends AbstractController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public LoginResponseUserDTO login(@RequestBody LoginRequestUserDTO loginDTO, HttpSession session) {
        LoginResponseUserDTO responseDTO = this.userService.login(loginDTO);
        session.setAttribute("LoggedUser", responseDTO.getId());
        return responseDTO;
    }

    @PostMapping("/register")
    public RegisterResponseUserDTO register(@RequestBody RegisterRequestUserDTO registerDTO) {
        return this.userService.register(registerDTO);
    }
}