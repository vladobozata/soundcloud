package com.soundcloud.model.DTOs.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@NoArgsConstructor
public class FilterResponseUserWithoutPlaylistDTO {
    private int id;
    private String username;
    private int songs;
    private int comments;
    private int followers;

    public FilterResponseUserWithoutPlaylistDTO(int id, String username, int songs, int comments, int followers) {
        this.id = id;
        this.username = username;
        this.songs = songs;
        this.comments = comments;
        this.followers = followers;
    }
}