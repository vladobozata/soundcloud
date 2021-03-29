package com.soundcloud.controller;

import com.soundcloud.exceptions.BadRequestException;
import com.soundcloud.model.DTOs.User.*;
import com.soundcloud.model.DTOs.MessageDTO;
import com.soundcloud.model.POJOs.User;
import com.soundcloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
public class UserController extends AbstractController {
    private final UserService userService;
    private final SessionManager sessionManager;

    @Autowired
    public UserController(UserService userService, SessionManager sessionManager) {
        this.userService = userService;
        this.sessionManager = sessionManager;
    }

    @PostMapping("/register")
    public UserDTO register(@RequestBody RegisterRequestUserDTO registerDTO, HttpSession session) {
        User loggedUser = this.sessionManager.getLoggedUser(session);
        if(loggedUser != null){
            throw new BadRequestException("You have to logout and then register again!");
        }
        return this.userService.register(registerDTO);
    }

    @PostMapping("/login")
    public UserDTO login(@RequestBody LoginRequestUserDTO loginDTO, HttpSession session) {
        User loggedUser = this.sessionManager.getLoggedUser(session);
        if(loggedUser != null){
            throw new BadRequestException("You already logged in!");
        }
        UserDTO responseDTO = this.userService.login(loginDTO);
        this.sessionManager.loginUser(session, responseDTO.getId());
        return responseDTO;
    }

    @PostMapping("/logout")
    public MessageDTO logout(HttpSession session){
        this.sessionManager.validateUser(session, "You have to login and then logout!");
        this.sessionManager.logoutUser(session);
        return new MessageDTO("You successfully logout!");
    }

    @DeleteMapping("/remove-profile")
    public MessageDTO removeProfile(HttpSession session){
        User loggedUser = this.sessionManager.validateUser(session, "You have to login and then remove your profile!");
        return this.userService.removeProfile(loggedUser.getId());
    }

    @PostMapping("/follow-user")
    public MessageDTO followUser(@RequestBody FollowRequestUserDTO followDTO, HttpSession session) {
        User loggedUser = this.sessionManager.validateUser(session, "You have to login and then follow users!");
        return this.userService.followUser(followDTO, loggedUser);
    }

    @DeleteMapping("/unfollow-user")
    public MessageDTO unfollowUser(@RequestBody FollowRequestUserDTO unfollowDTO, HttpSession session) {
        User loggedUser = this.sessionManager.validateUser(session, "You have to login and then unfollow users!");
        return this.userService.unfollowUser(unfollowDTO, loggedUser);
    }

    @PutMapping("/update-profile")
    public MessageDTO updateProfile(@RequestBody UpdateRequestUserDTO updateDTO, HttpSession session){
        User loggedUser = this.sessionManager.validateUser(session, "You have to login and then update your profile!");
        return this.userService.updateProfile(updateDTO, loggedUser);
    }

    @GetMapping("/my-profile")
    public MyProfileResponseDTO viewProfile(HttpSession session){
        User loggedUser = this.sessionManager.validateUser(session, "You have to login and then view your profile!");
        return this.userService.viewMyProfile(loggedUser);
    }

    @GetMapping("/users/{username}")
    public UserDTO userInformation(@PathVariable String username){
        return this.userService.userInformation(username);
    }
}