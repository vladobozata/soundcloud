package com.soundcloud.model.DTOs.Song;

import com.soundcloud.model.POJOs.Song;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class SongFilterResponseDTO {
    private int id;
    private String title;
    private String uploadedBy;
    private int songId;
    private int views;
    private int comments;
    private int likes;
    private int dislikes;
    private LocalDateTime dateUploaded;

    public SongFilterResponseDTO(Song song) {
        id = song.getId();
        title = song.getTitle();
        uploadedBy = song.getOwner().getUsername();
        songId = song.getId();
        views = song.getViews();
        comments = song.getComments().size();
        likes = song.getLikers().size();
        dislikes = song.getDislikers().size();
        dateUploaded = song.getCreatedAt();
    }
}
