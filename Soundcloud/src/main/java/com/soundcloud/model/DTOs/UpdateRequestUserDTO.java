package com.soundcloud.model.DTOs;

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
public class UpdateRequestUserDTO {
    private int id;
    private String username;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
    private String email;
    private int age;
}