package com.soundcloud.model.DTOs.Comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PostCommentRequestDTO {
    private int songId;
    private String text;
}