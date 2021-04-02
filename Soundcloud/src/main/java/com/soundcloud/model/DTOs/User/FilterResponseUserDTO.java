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

    public FilterResponseUserDTO(int id, String username, int songs, int comments, int playlists, int followers) {
        this.id = id;
        this.username = username;
        this.songs = songs;
        this.comments = comments;
        this.playlists = playlists;
        this.followers = followers;
    }

    public FilterResponseUserDTO(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.songs = user.getSongs().size();
        this.comments = user.getComments().size();
        this.playlists = user.getPlaylists().size();
        this.followers = user.getFollowers().size();
    }
}