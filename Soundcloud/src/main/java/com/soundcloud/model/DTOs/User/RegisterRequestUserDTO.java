package com.soundcloud.model.DTOs.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestUserDTO {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private int age;
}