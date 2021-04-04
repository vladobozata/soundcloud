package com.soundcloud.controller;

import com.soundcloud.service.email.TokenService;
import com.soundcloud.exceptions.BadRequestException;
import com.soundcloud.model.DTOs.User.FilterRequestUserDTO;
import com.soundcloud.model.DTOs.User.*;
import com.soundcloud.model.DTOs.MessageDTO;
import com.soundcloud.model.POJOs.User;
import com.soundcloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;

@RestController
public class UserController extends AbstractController {
    private final UserService userService;
    private final SessionManager sessionManager;
    private final TokenService tokenService;

    @Autowired
    public UserController(UserService userService, SessionManager sessionManager, TokenService tokenService) {
        this.userService = userService;
        this.sessionManager = sessionManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/users")
    public UserProfileResponseDTO register(@RequestBody RegisterRequestUserDTO registerDTO, HttpSession session) {
        User loggedUser = this.sessionManager.getLoggedUser(session);
        if (loggedUser != null) {
            throw new BadRequestException("You have to logout and then register again!");
        }
        return new UserProfileResponseDTO(this.userService.register(registerDTO));
    }

    @PostMapping("/login")
    public UserProfileResponseDTO login(@RequestBody LoginRequestUserDTO loginDTO, HttpSession session) {
        User loggedUser = this.sessionManager.getLoggedUser(session);
        if (loggedUser != null) {
            throw new BadRequestException("You already logged in!");
        }
        UserProfileResponseDTO responseDTO = new UserProfileResponseDTO(this.userService.login(loginDTO));
        this.sessionManager.loginUser(session, responseDTO.getId());
        return responseDTO;
    }

    @PostMapping("/logout")
    public MessageDTO logout(HttpSession session) {
        this.sessionManager.validateUser(session, "You have to login and then logout!");
        this.sessionManager.logoutUser(session);
        return new MessageDTO("You successfully logout!");
    }

    @PostMapping("/users/follow")
    public FollowResponseUserDTO followUser(@RequestBody FollowRequestUserDTO followDTO, HttpSession session) {
        User loggedUser = this.sessionManager.validateUser(session, "You have to login and then follow users!");
        FollowResponseUserDTO followResponseDTO = new FollowResponseUserDTO(this.userService.followUser(followDTO, loggedUser));
        followResponseDTO.setFollowedByMe(true);
        followResponseDTO.setFollowing(loggedUser.getFollowed().size());
        return followResponseDTO;
    }

    @PostMapping("/users/filter")
    public List<FilterResponseUserWithoutPlaylistDTO> filterUsers(@RequestBody FilterRequestUserDTO filterUserDTO) {
        return this.userService.filterUsers(filterUserDTO);
    }

    @PutMapping("/users")
    public UserProfileResponseDTO updateProfile(@RequestBody UpdateRequestUserDTO updateDTO, HttpSession session) {
        User loggedUser = this.sessionManager.validateUser(session, "You have to login and then update your profile!");
        return new UserProfileResponseDTO(this.userService.updateProfile(updateDTO, loggedUser));
    }

    @DeleteMapping("/users")
    public MessageDTO removeProfile(HttpSession session) {
        User loggedUser = this.sessionManager.validateUser(session, "You have to login and then remove your profile!");
        this.userService.removeProfile(loggedUser.getId());
        return new MessageDTO("Your profile was removed!");
    }

    @DeleteMapping("/users/unfollow")
    public FollowResponseUserDTO unfollowUser(@RequestBody FollowRequestUserDTO unfollowDTO, HttpSession session) {
        User loggedUser = this.sessionManager.validateUser(session, "You have to login and then unfollow users!");
        FollowResponseUserDTO unfollowResponseDTO = new FollowResponseUserDTO(this.userService.unfollowUser(unfollowDTO, loggedUser));
        unfollowResponseDTO.setFollowedByMe(false);
        unfollowResponseDTO.setFollowing(loggedUser.getFollowed().size());
        return unfollowResponseDTO;
    }

    @GetMapping("/users")
    public UserProfileResponseDTO viewProfile(HttpSession session) {
        User loggedUser = this.sessionManager.validateUser(session, "You have to login and then view your profile!");
        return new UserProfileResponseDTO(this.userService.viewMyProfile(loggedUser.getId()));
    }

    @GetMapping("/users/{username}")
    public FilterResponseUserDTO userInformation(@PathVariable String username) {
        return new FilterResponseUserDTO(this.userService.userInformation(username));
    }

    @GetMapping("/verify/{token}")
    public MessageDTO verify(@PathVariable String token, HttpSession session) {
        User loggedUser = this.sessionManager.getLoggedUser(session);
        this.tokenService.confirmToken(token, loggedUser);
        return new MessageDTO("Email confirmed!");
    }
}