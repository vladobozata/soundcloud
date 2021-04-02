package com.soundcloud.model.DTOs.User;

import com.soundcloud.model.POJOs.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@NoArgsConstructor
public class FilterResponseUserDTO {
    private int id;
    private String username;
    private int songs;
    private int comments;
    private int playlists;
    private int followers;
    private int followed;

    public FilterResponseUserDTO(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.songs = user.getSongs().size();
        this.comments = user.getComments().size();
        this.playlists = user.getPlaylists().size();
        this.followers = user.getFollowers().size();
        this.followed = user.getFollowed().size();
    }
}