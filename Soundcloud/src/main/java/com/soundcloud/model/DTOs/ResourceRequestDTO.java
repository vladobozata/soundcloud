package com.soundcloud.model.DTOs;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ResourceRequestDTO {
    @JsonAlias("id")
    private Integer resourceId;
}