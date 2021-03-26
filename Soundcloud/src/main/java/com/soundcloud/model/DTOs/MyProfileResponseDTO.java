package com.soundcloud.model.DTOs;

import com.soundcloud.model.POJOs.Comment;
import com.soundcloud.model.POJOs.Playlist;
import com.soundcloud.model.POJOs.Song;
import com.soundcloud.model.POJOs.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Component
@NoArgsConstructor
@AllArgsConstructor
public class MyProfileResponseDTO {
    private String username;
    private String email;
    private int age;
    private LocalDateTime createdAt;
    private List<Comment> comments;
    private List<Playlist> playlists;
    private List<Song> songs;

    public MyProfileResponseDTO(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.age = user.getAge();
        this.createdAt = user.getCreatedAt();
        this.comments = user.getComments();
        this.playlists = user.getPlaylists();
        this.songs = user.getSongs();
    }
}