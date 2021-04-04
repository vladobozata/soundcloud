package com.soundcloud.service;

import java.util.*;

import com.soundcloud.model.repositories.VerificationTokenRepository;
import com.soundcloud.service.email.EmailService;
import com.soundcloud.exceptions.AuthenticationException;
import com.soundcloud.exceptions.BadRequestException;
import com.soundcloud.exceptions.NotFoundException;
import com.soundcloud.model.DAOs.UserDAO;
import com.soundcloud.model.DTOs.User.FilterRequestUserDTO;
import com.soundcloud.model.DTOs.User.*;
import com.soundcloud.model.POJOs.User;
import com.soundcloud.model.POJOs.VerificationToken;
import com.soundcloud.model.repositories.UserRepository;
import com.soundcloud.util.Validator;
import com.soundcloud.util.Order;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final UserDAO userDAO;
    private final static String FILTER_BY_SONGS = "songs";
    private final static String FILTER_BY_COMMENTS = "comments";
    private final static String FILTER_BY_PLAYLISTS = "playlists";
    private final static String FILTER_BY_FOLLOWERS = "followers";

    @Autowired
    public UserService(UserRepository userRepository, VerificationTokenRepository tokenRepository, UserDAO userDAO, EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.userDAO = userDAO;
        Validator.userRepository = this.userRepository;
    }

    public void validateUser(User user) {
        if (user == null) {
            throw new NotFoundException("User not found!");
        }
    }

    @Transactional
    public User register(RegisterRequestUserDTO registerDTO) {
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
        VerificationToken token = new VerificationToken(user);
        user = this.userRepository.save(user);
        this.tokenRepository.save(token);

        Thread emailThread = new Thread(() -> this.emailService.send(registerDTO.getEmail(), token.getToken()));
        emailThread.start();
        return user;
    }

    public User login(LoginRequestUserDTO loginDTO) {
        User user = this.userRepository.findUserByUsername(loginDTO.getUsername());
        if (user != null) {
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            if (encoder.matches(loginDTO.getPassword(), user.getPassword())) {
                if (user.isEnabled()) {
                    return user;
                }
                throw new AuthenticationException("You must verify your email!");
            }
        }
        throw new BadRequestException("Wrong credentials!");
    }

    @Transactional
    public User followUser(FollowRequestUserDTO followDTO, User loggedUser) {
        User user = this.userRepository.findUserById(followDTO.getUserID());
        validateUser(user);
        if (followDTO.getUserID() == loggedUser.getId()) {
            throw new BadRequestException("You can`t un/follow yourself!");
        }
        if (user.getFollowers().contains(loggedUser)) {
            throw new BadRequestException("You already follow " + user.getUsername() + "!");
        }
        user.getFollowers().add(loggedUser);
        loggedUser.getFollowed().add(user);
        return this.userRepository.save(user);
    }

    @Transactional
    public User unfollowUser(FollowRequestUserDTO followDTO, User loggedUser) {
        User user = this.userRepository.findUserById(followDTO.getUserID());
        validateUser(user);
        if (followDTO.getUserID() == loggedUser.getId()) {
            throw new BadRequestException("You can`t un/follow yourself!");
        }
        if (!user.getFollowers().contains(loggedUser)) {
            throw new BadRequestException("You do not follow " + user.getUsername() + "!");
        }
        user.getFollowers().remove(loggedUser);
        loggedUser.getFollowed().remove(user);
        return this.userRepository.save(user);
    }

    public User updateProfile(UpdateRequestUserDTO updateDTO, User loggedUser) {
        Validator.validateAge(updateDTO.getAge(), loggedUser);
        Validator.updateUsername(updateDTO.getUsername(), loggedUser);
        Validator.updatePassword(updateDTO, loggedUser);
        Validator.updateEmail(updateDTO.getEmail(), loggedUser);
        return this.userRepository.save(loggedUser);
    }

    public User userInformation(String username) {
        User user = this.userRepository.findUserByUsername(username);
        validateUser(user);
        return user;
    }

    public User viewMyProfile(int userID) {
        return this.userRepository.findUserById(userID);
    }

    @Transactional
    public void removeProfile(int userID) {
        this.userRepository.deleteUserById(userID);
    }

    @SneakyThrows
    public List<FilterResponseUserWithoutPlaylistDTO> filterUsers(FilterRequestUserDTO filterUserDTO) {
        if (!filterUserDTO.getOrderBy().equalsIgnoreCase(Order.ASC.toString())) {
            if (!filterUserDTO.getOrderBy().equalsIgnoreCase(Order.DESC.toString())) {
                throw new BadRequestException("Invalid order type!");
            }
        }
        if (filterUserDTO.getPage() <= 0) {
            throw new BadRequestException("Page number must be at least 1!");
        }
        if (filterUserDTO.getItemsPerPage() <= 0) {
            throw new BadRequestException("Items per page must be at least 1!");
        }
        switch (filterUserDTO.getSortBy()) {
            case FILTER_BY_COMMENTS:
            case FILTER_BY_FOLLOWERS:
            case FILTER_BY_PLAYLISTS:
            case FILTER_BY_SONGS:
                return this.userDAO.getFilteredUsers(filterUserDTO);
            default:
                throw new BadRequestException("Sort type not recognized!");
        }
    }
}