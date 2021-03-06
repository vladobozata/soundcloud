package com.soundcloud.model.DTOs.Song;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SongFilterRequestDTO {
    private String title;
    private String sortBy;
    private String orderBy;
    private Integer page;
}