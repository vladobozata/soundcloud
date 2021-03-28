package com.soundcloud.util;

import com.soundcloud.exceptions.BadRequestException;
import com.soundcloud.model.DTOs.UpdateRequestUserDTO;
import com.soundcloud.model.POJOs.User;
import com.soundcloud.model.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
    private static final String PASSWORD_PATTERN = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^*&+=]).{8,}";
    private static final String EMAIL_PATTERN = "^[a-z]+[A-Za-z0-9_.-]{4,}+@[a-z]{2,6}+\\.[a-z]{2,5}";
    private static final String USERNAME_PATTERN = "^[A-Za-z]\\w{3,29}$";
    public static UserRepository userRepository;

    public static void updateUsername(String username, User loggedUser) {
        if (!username.equals("")) {
            if (!validateName(username)) {
                throw new BadRequestException("Username format is not correct!");
            }
            if (userRepository.findUserByUsername(username) != null) {
                throw new BadRequestException("Username already exists!");
            }
            loggedUser.setUsername(username);
        }
    }

    public static void updatePassword(UpdateRequestUserDTO updateDTO, User loggedUser) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!updateDTO.getOldPassword().equals("")) {
            if (!validatePassword(updateDTO.getNewPassword())) {
                throw new BadRequestException("Password format is not correct!");
            }
            if (!updateDTO.getNewPassword().equals(updateDTO.getConfirmPassword())) {
                throw new BadRequestException("Passwords are not equals!");
            }
            if (!encoder.matches(updateDTO.getOldPassword(), loggedUser.getPassword())) {
                throw new BadRequestException("Old password is incorrect!");
            }
            loggedUser.setPassword(encoder.encode(updateDTO.getConfirmPassword()));
        }
    }

    public static void updateEmail(String email, User loggedUser) {
        if (!email.equals("")) {
            if (!validateEmail(email)) {
                throw new BadRequestException("Email format is not correct!");
            }
            if (userRepository.findByEmail(email) != null) {
                throw new BadRequestException("Email already exists!");
            }
            loggedUser.setEmail(email);
        }
    }

    public static boolean validateName(String username) {
        Pattern pattern = Pattern.compile(USERNAME_PATTERN);
        Matcher matcher = pattern.matcher(username);

        return matcher.matches();
    }

    public static boolean validatePassword(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

    public static boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
}