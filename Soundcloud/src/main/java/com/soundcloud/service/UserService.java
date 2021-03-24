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

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public RegisterResponseUserDTO register(RegisterRequestUserDTO userDTO){
        if(!userDTO.getPassword().equals(userDTO.getConfirmPassword())){
            throw new BadRequestException("Passwords are not equals!");
        }
        if(this.userRepository.findByEmail(userDTO.getEmail()) != null){
            throw new BadRequestException("Email already exists!");
        }
        if(this.userRepository.findByUsername(userDTO.getUsername()) != null){
            throw new BadRequestException("Username already exists!");
        }
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        userDTO.setPassword(encoder.encode(userDTO.getPassword()));

        User user = new User(userDTO);
        user = this.userRepository.save(user);
        return new RegisterResponseUserDTO(user);
    }


    public LoginResponseUserDTO login(LoginRequestUserDTO loginDTO) {
        User user = this.userRepository.findByUsername(loginDTO.getUsername());
        if(user == null){
            throw new AuthenticationException("Wrong credentials");
        }
        else{
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            if(encoder.matches(loginDTO.getPassword(), user.getPassword())){
                return new LoginResponseUserDTO(user);
            }
            else{
                throw new AuthenticationException("Wrong credentials");
            }
        }
    }


}