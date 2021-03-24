package com.soundcloud.controller;

import com.soundcloud.model.DTOs.LoginResponseUserDTO;
import com.soundcloud.model.DTOs.LoginRequestUserDTO;
import com.soundcloud.model.DTOs.RegisterRequestUserDTO;
import com.soundcloud.model.DTOs.RegisterResponseUserDTO;
import com.soundcloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class UserController extends AbstractController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public LoginResponseUserDTO login(@RequestBody LoginRequestUserDTO loginDTO, HttpSession session) {
        LoginResponseUserDTO resposeDTO = this.userService.login(loginDTO);
        session.setAttribute("LoggedUser", resposeDTO.getId());
        return resposeDTO;
    }

    @PostMapping("/register")
    public RegisterResponseUserDTO register(@RequestBody RegisterRequestUserDTO registerDTO) {
        return this.userService.register(registerDTO);
    }
}