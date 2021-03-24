package com.soundcloud.model.DTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@Component
public class RegisterRequestUserDTO {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private int age;
}