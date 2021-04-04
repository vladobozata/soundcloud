package com.soundcloud.model.DTOs.Song;

import com.soundcloud.model.POJOs.Song;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class SongUploadResponseDTO {
    private int id;
    private String title;
    private String url;
    private LocalDateTime createdAt;

    public SongUploadResponseDTO(Song song) {
        this.id = song.getId();
        this.title = song.getTitle();
        this.url = song.getUrl();
        this.createdAt = song.getCreatedAt();
    }
}