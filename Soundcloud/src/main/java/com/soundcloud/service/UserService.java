package com.soundcloud.service;

import com.soundcloud.exceptions.AuthenticationException;
import com.soundcloud.exceptions.BadRequestException;
import com.soundcloud.model.DTOs.LoginResponseUserDTO;
import com.soundcloud.model.DTOs.LoginRequestUserDTO;
import com.soundcloud.model.DTOs.RegisterRequestUserDTO;
import com.soundcloud.model.DTOs.RegisterResponseUserDTO;
import com.soundcloud.model.POJOs.User;
import com.soundcloud.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public RegisterResponseUserDTO register(RegisterRequestUserDTO registerDTO){
        if(!validateEmail(registerDTO.getEmail())){
            throw new BadRequestException("Email format is not correct!");
        }
        if(!validatePassword(registerDTO.getPassword())){
            throw new BadRequestException("Password format is not correct!");
        }
        if(!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())){
            throw new BadRequestException("Passwords are not equals!");
        }
        if(this.userRepository.findByEmail(registerDTO.getEmail()) != null){
            throw new BadRequestException("Email already exists!");
        }
        if(this.userRepository.findByUsername(registerDTO.getUsername()) != null){
            throw new BadRequestException("Username already exists!");
        }
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        registerDTO.setPassword(encoder.encode(registerDTO.getPassword()));

        User user = new User(registerDTO);
        user = this.userRepository.save(user);
        return new RegisterResponseUserDTO(user);
    }

    private boolean validatePassword(String password){
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

    private boolean validateEmail(String email){
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }


    public LoginResponseUserDTO login(LoginRequestUserDTO loginDTO) {
        User user = this.userRepository.findByUsername(loginDTO.getUsername());
        if(user == null){
            throw new AuthenticationException("Wrong credentials!");
        }
        else{
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            if(encoder.matches(loginDTO.getPassword(), user.getPassword())){
                return new LoginResponseUserDTO(user);
            }
            else{
                throw new AuthenticationException("Wrong credentials!");
            }
        }
    }


}