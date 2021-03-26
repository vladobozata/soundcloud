package com.soundcloud.model.DTOs;

import com.soundcloud.model.POJOs.Playlist;
import com.soundcloud.model.POJOs.Song;
import com.soundcloud.model.POJOs.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Component
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistResponseDTO {
    private String name;
    private User owner;
    private List<Song> songs;
    private LocalDateTime createdAt;

    public PlaylistResponseDTO(Playlist playlist){
        this.name = playlist.getName();
        this.owner = playlist.getOwner();
        this.songs = playlist.getSongs();
        this.createdAt = LocalDateTime.now();
    }
}