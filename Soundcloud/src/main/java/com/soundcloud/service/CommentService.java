package com.soundcloud.service;

import com.soundcloud.exceptions.NotFoundException;
import com.soundcloud.model.DTOs.Comment.PostCommentRequestDTO;
import com.soundcloud.model.DTOs.MessageDTO;
import com.soundcloud.model.POJOs.Comment;
import com.soundcloud.model.POJOs.Song;
import com.soundcloud.model.POJOs.User;
import com.soundcloud.model.repositories.CommentRepository;
import com.soundcloud.model.repositories.SongRepository;
import com.soundcloud.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, SongRepository songRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public MessageDTO postComment(PostCommentRequestDTO requestDTO, User loggedUser) {
        Song songCommented = songRepository.getSongById(requestDTO.getSongId());
        if (songCommented == null) throw new NotFoundException("Cannot find song id#" + requestDTO.getSongId());

        Comment comment = new Comment(requestDTO.getText());

        // Add comment to song
        songCommented.getComments().add(comment);
        // Add comment to user
        loggedUser.getComments().add(comment);

        comment.setOwner(loggedUser);
        comment.setSong(songCommented);

        userRepository.save(loggedUser);
        songRepository.save(songCommented);
        commentRepository.save(comment);
        return new MessageDTO("Comment posted. Comment id: #" + comment.getId());
    }
}
