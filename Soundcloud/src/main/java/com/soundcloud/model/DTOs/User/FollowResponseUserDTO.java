package com.soundcloud.model.DTOs.User;

import com.soundcloud.model.POJOs.User;
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
public class FollowResponseUserDTO {
    private int userId;
    private String username;
    private int following;
    private boolean followedByMe;

    public FollowResponseUserDTO(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
    }
}