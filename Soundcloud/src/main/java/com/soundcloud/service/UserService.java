package com.soundcloud.service;

import com.soundcloud.exceptions.AuthenticationException;
import com.soundcloud.exceptions.BadRequestException;
import com.soundcloud.exceptions.NotFoundException;
import com.soundcloud.model.DTOs.*;
import com.soundcloud.model.POJOs.User;
import com.soundcloud.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {
    private static final String PASSWORD_PATTERN = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^*&+=]).{8,}";
    private static final String EMAIL_PATTERN = "^[a-z]+[A-Za-z0-9_.-]{4,}+@[a-z]{2,6}+\\.[a-z]{2,5}";
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO register(RegisterRequestUserDTO registerDTO) {
        if (!validateEmail(registerDTO.getEmail())) {
            throw new BadRequestException("Email format is not correct!");
        }
        if (!validatePassword(registerDTO.getPassword())) {
            throw new BadRequestException("Password format is not correct!");
        }
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BadRequestException("Passwords are not equals!");
        }
        if (this.userRepository.findByEmail(registerDTO.getEmail()) != null) {
            throw new BadRequestException("Email already exists!");
        }
        if (this.userRepository.findByUsername(registerDTO.getUsername()) != null) {
            throw new BadRequestException("Username already exists!");
        }
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        registerDTO.setPassword(encoder.encode(registerDTO.getPassword()));

        User user = new User(registerDTO);
        user = this.userRepository.save(user);
        return new UserDTO(user);
    }

    private boolean validatePassword(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

    private boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    public UserDTO login(LoginRequestUserDTO loginDTO) {
        User user = this.userRepository.findByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new AuthenticationException("Wrong credentials!");
        } else {
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            if (encoder.matches(loginDTO.getPassword(), user.getPassword())) {
                return new UserDTO(user);
            } else {
                throw new AuthenticationException("Wrong credentials!");
            }
        }
    }

    public UserDTO userInformation(String username) {
        User user = this.userRepository.findUserByUsername(username);
        if (user == null) {
            throw new NotFoundException("User with this username not found!");
        }
        return new UserDTO(user);
    }

    private void updatePassword(UpdateRequestUserDTO updateDTO, User loggedUser) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!updateDTO.getOldPassword().equals("")) {
            if (!encoder.matches(updateDTO.getOldPassword(), loggedUser.getPassword())) {
                throw new BadRequestException("Old password is incorrect!");
            }
            if (!updateDTO.getNewPassword().equals(updateDTO.getConfirmPassword())) {
                throw new BadRequestException("Passwords are not equals!");
            }
            if (!validatePassword(updateDTO.getNewPassword())) {
                throw new BadRequestException("Password format is not correct!");
            }
            loggedUser.setPassword(encoder.encode(updateDTO.getConfirmPassword()));
        }
    }

    private void updateEmail(UpdateRequestUserDTO updateDTO, User loggedUser) {
        if (!updateDTO.getEmail().equals("")) {
            if (!validateEmail(updateDTO.getEmail())) {
                throw new BadRequestException("Email format is not correct!");
            }
            if (this.userRepository.findByEmail(updateDTO.getEmail()) != null) {
                throw new BadRequestException("Email already exists!");
            }
            loggedUser.setEmail(updateDTO.getEmail());
        }
    }

    @Transactional
    public UserMessageDTO updateProfile(UpdateRequestUserDTO updateDTO, User loggedUser) {
        if (updateDTO.getAge() != 0) {
            loggedUser.setAge(updateDTO.getAge());
        }
        if (this.userRepository.findByUsername(updateDTO.getUsername()) != null) {
            throw new BadRequestException("Username already exists!");
        } else {
            //TODO
            if(updateDTO.getUsername().isEmpty()){
                throw new BadRequestException("Username can`t be an empty string!");
            }
            loggedUser.setUsername(updateDTO.getUsername());
        }
        updatePassword(updateDTO, loggedUser);
        updateEmail(updateDTO, loggedUser);
        this.userRepository.save(loggedUser);
        return new UserMessageDTO("You successfully updated your profile!");
    }

    public MyProfileResponseDTO viewMyProfile(User loggedUser) {
        User user = this.userRepository.findUserByUsername(loggedUser.getUsername());
        return new MyProfileResponseDTO(user);
    }

    public UnfollowResponseUserDTO unfollowUser(UnfollowRequestUserDTO unfollowDTO, User loggedUser) {
        return null;
    }

    public FollowResponseUserDTO followUser(FollowRequestUserDTO followDTO, User loggedUser) {
        return null;
    }

    @Transactional
    public UserMessageDTO removeProfile(User loggedUser) {
        this.userRepository.deleteByUsername(loggedUser.getUsername());
        return new UserMessageDTO("Your profile was removed!");
    }
}