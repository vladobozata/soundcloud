package com.soundcloud.service;

import java.util.*;

import com.soundcloud.exceptions.AuthenticationException;
import com.soundcloud.exceptions.BadRequestException;
import com.soundcloud.exceptions.NotFoundException;
import com.soundcloud.model.DAOs.UserDAO;
import com.soundcloud.model.DTOs.User.FilterRequestUserDTO;
import com.soundcloud.model.DTOs.User.*;
import com.soundcloud.model.DTOs.MessageDTO;
import com.soundcloud.model.POJOs.User;
import com.soundcloud.model.repositories.UserRepository;
import com.soundcloud.util.Validator;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserDAO userDAO;
    private final static String FILTER_BY_SONGS = "songs";
    private final static String FILTER_BY_COMMENTS = "comments";
    private final static String FILTER_BY_PLAYLISTS = "playlists";
    private final static String FILTER_BY_FOLLOWERS = "followers";

    @Autowired
    public UserService(UserRepository userRepository, UserDAO userDAO) {
        this.userRepository = userRepository;
        this.userDAO = userDAO;
        Validator.userRepository = this.userRepository;
    }

    public void validateUser(User user) {
        if (user == null) {
            throw new NotFoundException("User not found!");
        }
    }

    public UserProfileResponseDTO register(RegisterRequestUserDTO registerDTO) {
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
        return new UserProfileResponseDTO(user);
    }

    public UserProfileResponseDTO login(LoginRequestUserDTO loginDTO) {
        User user = this.userRepository.findUserByUsername(loginDTO.getUsername());
        if (user != null) {
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            if (encoder.matches(loginDTO.getPassword(), user.getPassword())) {
                return new UserProfileResponseDTO(user);
            }
        }
        throw new AuthenticationException("Wrong credentials!");
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

    public UserProfileResponseDTO userInformation(String username) {
        User user = this.userRepository.findUserByUsername(username);
        validateUser(user);
        return new UserProfileResponseDTO(user);
    }

    public UserProfileResponseDTO updateProfile(UpdateRequestUserDTO updateDTO, User loggedUser) {
        if (updateDTO.getAge() > 0) {
            loggedUser.setAge(updateDTO.getAge());
        }
        Validator.updateUsername(updateDTO.getUsername(), loggedUser);
        Validator.updatePassword(updateDTO, loggedUser);
        Validator.updateEmail(updateDTO.getEmail(), loggedUser);
        User user = this.userRepository.save(loggedUser);
        return new UserProfileResponseDTO(user);
    }

    public UserProfileResponseDTO viewMyProfile(User loggedUser) {
        User user = this.userRepository.findUserByUsername(loggedUser.getUsername());
        return new UserProfileResponseDTO(user);
    }

    @Transactional
    public MessageDTO removeProfile(int userID) {
        this.userRepository.deleteUserById(userID);
        return new MessageDTO("Your profile was removed!");
    }

    @SneakyThrows
    public List<FilterResponseUserDTO> filterUsers(FilterRequestUserDTO filterUserDTO) {
        if (!filterUserDTO.getOrderBy().equalsIgnoreCase("ASC")) {
            if (!filterUserDTO.getOrderBy().equalsIgnoreCase("DESC")) {
                throw new BadRequestException("Invalid order type!");
            }
        }
        switch (filterUserDTO.getSortBy()) {
            case FILTER_BY_COMMENTS:
            case FILTER_BY_FOLLOWERS:
            case FILTER_BY_PLAYLISTS:
            case FILTER_BY_SONGS:
                return this.userDAO.getFilteredUsers(filterUserDTO);
            default:
                throw new NotFoundException("Sort type not found!");
        }
    }
}