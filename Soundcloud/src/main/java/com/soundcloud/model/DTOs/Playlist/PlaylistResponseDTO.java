package com.soundcloud.model.DTOs.Playlist;

import com.soundcloud.model.DTOs.Song.SongFilterResponseDTO;
import com.soundcloud.model.POJOs.Playlist;
import com.soundcloud.model.POJOs.Song;
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
public class PlaylistResponseDTO {
    private int id;
    private String name;
    private String ownerName;
    private LocalDateTime createdAt;
    private List<SongFilterResponseDTO> songs;

    public PlaylistResponseDTO(Playlist playlist){
        this.id = playlist.getId();
        this.name = playlist.getName();
        this.ownerName = playlist.getOwner().getUsername();
        this.createdAt = playlist.getCreatedAt();
        this.songs = new ArrayList<>();
        for(Song song : playlist.getSongs()){
            this.songs.add(new SongFilterResponseDTO(song));
        }
    }
}