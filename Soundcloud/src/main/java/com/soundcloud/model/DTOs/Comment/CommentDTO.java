package com.soundcloud.model.DTOs.Comment;

import com.soundcloud.model.POJOs.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class CommentDTO {
    private int id;
    private String owner;
    private String text;

    public CommentDTO(Comment comment) {
        this.id = comment.getId();
        this.owner = comment.getOwner().getUsername();
        this.text = comment.getText();
    }
}
