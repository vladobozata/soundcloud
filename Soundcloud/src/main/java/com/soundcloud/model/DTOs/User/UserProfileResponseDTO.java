package com.soundcloud.model.DTOs.User;

import com.soundcloud.model.DTOs.Comment.CommentResponseDTO;
import com.soundcloud.model.DTOs.Playlist.PlaylistResponseDTO;
import com.soundcloud.model.DTOs.Song.SongFilterResponseDTO;
import com.soundcloud.model.POJOs.Comment;
import com.soundcloud.model.POJOs.Playlist;
import com.soundcloud.model.POJOs.Song;
import com.soundcloud.model.POJOs.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDTO {
    private int id;
    private String username;
    private String email;
    private int age;
    private int followers;
    private int followed;
    private LocalDateTime createdAt;
    private List<CommentResponseDTO> comments;
    private List<PlaylistResponseDTO> playlists;
    private List<SongFilterResponseDTO> songs;

    public UserProfileResponseDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.age = user.getAge();
        this.createdAt = user.getCreatedAt();
        this.followers = user.getFollowers().size();
        this.followed = user.getFollowed().size();
        this.playlists = new ArrayList<>();
        this.songs = new ArrayList<>();
        this.comments = new ArrayList<>();
        for(Comment comment : user.getComments()){
            this.comments.add(new CommentResponseDTO(comment));
        }
        for(Playlist playlist : user.getPlaylists()){
            this.playlists.add(new PlaylistResponseDTO(playlist));
        }
        for(Song song : user.getSongs()){
            this.songs.add(new SongFilterResponseDTO(song));
        }
    }
}