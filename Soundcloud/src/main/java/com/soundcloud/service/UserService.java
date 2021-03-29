package com.soundcloud.service;

import com.soundcloud.exceptions.AuthenticationException;
import com.soundcloud.exceptions.BadRequestException;
import com.soundcloud.exceptions.NotFoundException;
import com.soundcloud.model.DTOs.User.*;
import com.soundcloud.model.DTOs.MessageDTO;
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

    public void validateUser(User user){
        if (user == null) {
            throw new NotFoundException("User not found!");
        }
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

    public MessageDTO followUser(FollowRequestUserDTO followDTO, User loggedUser) {
        User user = this.userRepository.findUserById(followDTO.getUserID());
        validateUser(user);
        if (followDTO.getUserID() == loggedUser.getId()) {
            throw new BadRequestException("You can`t un/follow yourself!");
        }
        if (user.getFollowers().contains(loggedUser)) {
            throw new BadRequestException("You already follow " + user.getUsername() + "!");
        }
        user.getFollowers().add(loggedUser);
        this.userRepository.save(user);
        return new MessageDTO("You successfully followed " + user.getUsername() + "!");
    }

    public MessageDTO unfollowUser(FollowRequestUserDTO followDTO, User loggedUser) {
        User user = this.userRepository.findUserById(followDTO.getUserID());
        validateUser(user);
        if (followDTO.getUserID() == loggedUser.getId()) {
            throw new BadRequestException("You can`t un/follow yourself!");
        }
        if (!user.getFollowers().contains(loggedUser)) {
            throw new BadRequestException("You do not follow " + user.getUsername() + "!");
        }
        user.getFollowers().remove(loggedUser);
        this.userRepository.save(user);
        return new MessageDTO("You successfully unfollowed " + user.getUsername() + "!");
    }

    public UserDTO userInformation(String username) {
        User user = this.userRepository.findUserByUsername(username);
        validateUser(user);
        return new UserDTO(user);
    }

    public MessageDTO updateProfile(UpdateRequestUserDTO updateDTO, User loggedUser) {
        if (updateDTO.getAge() > 0) {
            loggedUser.setAge(updateDTO.getAge());
        }
        Validator.updateUsername(updateDTO.getUsername(), loggedUser);
        Validator.updatePassword(updateDTO, loggedUser);
        Validator.updateEmail(updateDTO.getEmail(), loggedUser);
        this.userRepository.save(loggedUser);
        return new MessageDTO("You successfully updated your profile!");
    }

    public MyProfileResponseDTO viewMyProfile(User loggedUser) {
        User user = this.userRepository.findUserByUsername(loggedUser.getUsername());
        return new MyProfileResponseDTO(user);
    }

    @Transactional
    public MessageDTO removeProfile(int userID) {
        this.userRepository.deleteUserById(userID);
        return new MessageDTO("Your profile was removed!");
    }
}