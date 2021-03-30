package com.soundcloud.model.DTOs.User;

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
}