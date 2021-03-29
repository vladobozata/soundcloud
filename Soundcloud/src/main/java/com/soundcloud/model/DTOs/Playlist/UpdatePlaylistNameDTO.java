package com.soundcloud.model.DTOs.Playlist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePlaylistNameDTO {
    private int playlistID;
    private String name;
}