package com.soundcloud.model.DTOs.Playlist;

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
    private String ownerName;
    private LocalDateTime createdAt;
    private List<Song> songs;

    public PlaylistResponseDTO(Playlist playlist){
        this.name = playlist.getName();
        this.ownerName = playlist.getOwner().getUsername();
        this.createdAt = playlist.getCreatedAt();
        this.songs = playlist.getSongs();
    }
}