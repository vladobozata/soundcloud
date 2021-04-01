package com.soundcloud.model.DTOs.Comment;

import com.soundcloud.model.POJOs.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Getter
@Setter
@Component
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {
    private int id;
    private String text;
    private LocalDateTime createdAt;
    private int likers;
    private int dislikers;

    public CommentResponseDTO(Comment comment) {
        this.id = comment.getId();
        this.text = comment.getText();
        this.createdAt = comment.getCreatedAt();
        this.likers = comment.getLikers().size();
        this.dislikers = comment.getDislikers().size();
    }
}