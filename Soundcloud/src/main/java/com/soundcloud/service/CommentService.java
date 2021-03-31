package com.soundcloud.service;

import com.soundcloud.exceptions.AuthenticationException;
import com.soundcloud.exceptions.BadRequestException;
import com.soundcloud.exceptions.NotFoundException;
import com.soundcloud.model.DTOs.Comment.CommentResponseDTO;
import com.soundcloud.model.DTOs.Comment.PostCommentRequestDTO;
import com.soundcloud.model.DTOs.MessageDTO;
import com.soundcloud.model.DTOs.ResourceRequestDTO;
import com.soundcloud.model.POJOs.Comment;
import com.soundcloud.model.POJOs.Song;
import com.soundcloud.model.POJOs.User;
import com.soundcloud.model.repositories.CommentRepository;
import com.soundcloud.model.repositories.SongRepository;
import com.soundcloud.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

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

    public MessageDTO deleteComment(ResourceRequestDTO requestDTO, User loggedUser) {
        Integer id = requestDTO.getResourceId();
        if (id == null) throw new BadRequestException("Must select comment to delete by id.");
        Comment comment = commentRepository.findCommentById(id);
        if (comment == null) throw new NotFoundException("Comment id#" +id+ " was not found.");
        if (comment.getOwner().getId() != loggedUser.getId()) throw new AuthenticationException("Cannot delete comments from other users.");

        commentRepository.delete(comment);
        return new MessageDTO("Comment id#" +id+ " successfully deleted.");
    }

    public List<CommentResponseDTO> getCommentBySong(int songId) {
        Song song = songRepository.getSongById(songId);
        if(song == null) throw new NotFoundException("Song id#" +songId+ " was not found.");
        List<Comment> songComments = commentRepository.findCommentsBySong(song);

        return songComments.stream().map(CommentResponseDTO::new).collect(Collectors.toList());
    }

    public CommentResponseDTO getCommentById(int commentId) {
        Comment comment = commentRepository.findCommentById(commentId);
        if (comment == null) throw new NotFoundException("Comment id#" +commentId+ " was not found.");
        return new CommentResponseDTO(comment);
    }
}
