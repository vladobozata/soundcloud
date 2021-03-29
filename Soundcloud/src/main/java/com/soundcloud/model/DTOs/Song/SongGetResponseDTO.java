package com.soundcloud.model.DTOs.Song;

import com.soundcloud.model.DTOs.Comment.CommentDTO;
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
    private List<CommentDTO> comments;
    private List<UserDTO> likers;
    private List<UserDTO> dislikers;

    public SongGetResponseDTO(Song song) {
        super(song);
        views = song.getViews();
        comments = new ArrayList<>();
        likers = new ArrayList<>();
        dislikers = new ArrayList<>();

        for (Comment comment : song.getComments()) {
            comments.add(new CommentDTO(comment));
        }

        for (User liker : song.getLikers()) {
            likers.add(new UserDTO(liker));
        }

        for (User disliker : song.getDislikers()) {
            likers.add(new UserDTO(disliker));
        }
    }
}
