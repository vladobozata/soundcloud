package com.soundcloud.service;

import com.soundcloud.exceptions.AuthenticationException;
import com.soundcloud.exceptions.BadRequestException;
import com.soundcloud.exceptions.NotFoundException;
import com.soundcloud.model.DTOs.User.*;
import com.soundcloud.model.POJOs.User;
import com.soundcloud.model.repositories.UserRepository;
import com.soundcloud.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        Validator.userRepository = this.userRepository;
    }

    public UserDTO register(RegisterRequestUserDTO registerDTO) {
        if (!Validator.validateName(registerDTO.getUsername())) {
            throw new BadRequestException("Username format is not correct!");
        }
        if (!Validator.validatePassword(registerDTO.getPassword())) {
            throw new BadRequestException("Password format is not correct!");
        }
        if (!Validator.validateEmail(registerDTO.getEmail())) {
            throw new BadRequestException("Email format is not correct!");
        }
        if (this.userRepository.findUserByUsername(registerDTO.getUsername()) != null) {
            throw new BadRequestException("Username already exists!");
        }
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BadRequestException("Passwords are not equals!");
        }
        if (this.userRepository.findUserByEmail(registerDTO.getEmail()) != null) {
            throw new BadRequestException("Email already exists!");
        }
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        registerDTO.setPassword(encoder.encode(registerDTO.getPassword()));

        User user = new User(registerDTO);
        user = this.userRepository.save(user);
        return new UserDTO(user);
    }

    public UserDTO login(LoginRequestUserDTO loginDTO) {
        User user = this.userRepository.findUserByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new AuthenticationException("Wrong credentials!");
        }
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        if (encoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return new UserDTO(user);
        } else {
            throw new AuthenticationException("Wrong credentials!");
        }
    }

    public UserMessageDTO followUser(FollowRequestUserDTO followDTO, User loggedUser) {
        User user = this.userRepository.findUserById(followDTO.getUserID());
        if (user == null) {
            throw new NotFoundException("User not found!");
        }
        if(followDTO.getUserID() == loggedUser.getId()){
            throw new BadRequestException("You can`t un/follow yourself!");
        }
        for (User follower : user.getFollowers()) {
            if (follower.getId() == loggedUser.getId()) {
                throw new BadRequestException("You already follow " + user.getUsername() + "!");
            }
        }
        user.getFollowers().add(loggedUser);
        this.userRepository.save(user);
        return new UserMessageDTO("You successfully followed " + user.getUsername() + "!");
    }

    public UserMessageDTO unfollowUser(FollowRequestUserDTO followDTO, User loggedUser) {
        User user = this.userRepository.findUserById(followDTO.getUserID());
        if (user == null) {
            throw new NotFoundException("User not found!");
        }
        if(followDTO.getUserID() == loggedUser.getId()){
            throw new BadRequestException("You can`t un/follow yourself!");
        }
        for(User follower : user.getFollowers()){
            if(follower.getId() == loggedUser.getId()){
                user.getFollowers().remove(loggedUser);
                this.userRepository.save(user);
                return new UserMessageDTO("You successfully unfollowed " + user.getUsername() + "!");
            }
        }
        throw new BadRequestException("You do not follow " + user.getUsername() + "!");
    }

    public UserDTO userInformation(String username) {
        User user = this.userRepository.findUserByUsername(username);
        if (user == null) {
            throw new NotFoundException("User with this username not found!");
        }
        return new UserDTO(user);
    }

    public UserMessageDTO updateProfile(UpdateRequestUserDTO updateDTO, User loggedUser) {
        if (updateDTO.getAge() > 0) {
            loggedUser.setAge(updateDTO.getAge());
        }
        Validator.updateUsername(updateDTO.getUsername(), loggedUser);
        Validator.updatePassword(updateDTO, loggedUser);
        Validator.updateEmail(updateDTO.getEmail(), loggedUser);
        this.userRepository.save(loggedUser);
        return new UserMessageDTO("You successfully updated your profile!");
    }

    public MyProfileResponseDTO viewMyProfile(User loggedUser) {
        User user = this.userRepository.findUserByUsername(loggedUser.getUsername());
        return new MyProfileResponseDTO(user);
    }

    @Transactional
    public UserMessageDTO removeProfile(int userID) {
        this.userRepository.deleteUserById(userID);
        return new UserMessageDTO("Your profile was removed!");
    }
}