package com.soundcloud.model.DTOs.Song;

import com.soundcloud.model.DTOs.Comment.CommentDTO;
import com.soundcloud.model.DTOs.User.UserDTO;
import com.soundcloud.model.POJOs.Comment;
import com.soundcloud.model.POJOs.Song;
import com.soundcloud.model.POJOs.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class SongFilterResponseDTO {
    private int id;
    private String title;
    private int songId;
    private int views;
    private int comments;
    private int likes;
    private int dislikes;
    private LocalDateTime dateUploaded;

    public SongFilterResponseDTO(Song song) {
        id = song.getId();
        title = song.getTitle();
        songId = song.getId();
        views = song.getViews();
        comments = song.getComments().size();
        likes = song.getLikers().size();
        dislikes = song.getDislikers().size();
        dateUploaded = song.getCreatedAt();
    }
}
