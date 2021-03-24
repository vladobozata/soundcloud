package com.soundcloud.model.DTOs;

import com.soundcloud.model.POJOs.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@NoArgsConstructor
public class LoginResponseUserDTO {
    private int id;
    private String username;
    private String email;
    private int age;

    public LoginResponseUserDTO(User user){
        this.id =user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.age = user.getAge();
    }
}