package com.soundcloud.model.DTOs.Song;

import com.soundcloud.model.DTOs.Comment.CommentResponseDTO;
import com.soundcloud.model.DTOs.User.UserDTO;
import com.soundcloud.model.POJOs.Comment;
import com.soundcloud.model.POJOs.Song;
import com.soundcloud.model.POJOs.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class SongGetResponseDTO extends SongUploadResponseDTO {
    private int views;
    private List<CommentResponseDTO> comments;
    private List<UserDTO> likers;
    private List<UserDTO> dislikers;

    public SongGetResponseDTO(Song song) {
        super(song);
        this.views = song.getViews();
        this.comments = new ArrayList<>();
        this.likers = new ArrayList<>();
        this.dislikers = new ArrayList<>();

        for (Comment comment : song.getComments()) {
            this.comments.add(new CommentResponseDTO(comment));
        }

        for (User liker : song.getLikers()) {
            this.likers.add(new UserDTO(liker));
        }

        for (User disliker : song.getDislikers()) {
            this.likers.add(new UserDTO(disliker));
        }
    }
}