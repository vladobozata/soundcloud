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
    private int inPlaylists;
    private LocalDateTime createdAt;

    public SongFilterResponseDTO(Song song) {
        this.id = song.getId();
        this.title = song.getTitle();
        this.uploadedBy = song.getOwner().getUsername();
        this.songId = song.getId();
        this.views = song.getViews();
        this.comments = song.getComments().size();
        this.likes = song.getLikers().size();
        this.dislikes = song.getDislikers().size();
        this.inPlaylists = song.getPlaylists().size();
        this.createdAt = song.getCreatedAt();
    }
}