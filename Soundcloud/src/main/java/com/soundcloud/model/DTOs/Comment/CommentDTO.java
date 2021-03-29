package com.soundcloud.model.DTOs.Comment;

import com.soundcloud.model.POJOs.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class CommentDTO {
    private String text;

    public CommentDTO(Comment comment) {
        this.text = comment.getText();
    }
}
