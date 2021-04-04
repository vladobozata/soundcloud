package com.soundcloud.service;

import com.soundcloud.exceptions.AuthenticationException;
import com.soundcloud.exceptions.BadRequestException;
import com.soundcloud.exceptions.NotFoundException;
import com.soundcloud.model.DTOs.Comment.CommentResponseDTO;
import com.soundcloud.model.DTOs.Comment.EditCommentRequestDTO;
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
        Song songCommented = this.songRepository.getSongById(requestDTO.getSongId());
        if (songCommented == null) throw new NotFoundException("Cannot find song id#" + requestDTO.getSongId());

        Comment comment = new Comment(requestDTO.getText());

        // Add comment to song
        songCommented.getComments().add(comment);
        // Add comment to user
        loggedUser.getComments().add(comment);

        comment.setOwner(loggedUser);
        comment.setSong(songCommented);

        this.userRepository.save(loggedUser);
        this.songRepository.save(songCommented);
        this.commentRepository.save(comment);
        return new MessageDTO("Comment posted. Comment id: #" + comment.getId());
    }

    public MessageDTO deleteComment(ResourceRequestDTO requestDTO, User loggedUser) {
        Integer id = requestDTO.getResourceId();
        if (id == null) throw new BadRequestException("Must select comment to delete by id.");
        Comment comment = this.commentRepository.findCommentById(id);

        if (comment == null) throw new NotFoundException("Comment id#" + id + " was not found.");
        if (comment.getOwner().getId() != loggedUser.getId()) throw new AuthenticationException("Cannot delete comments from other users.");

        this.commentRepository.delete(comment);
        return new MessageDTO("Comment id#" + id + " successfully deleted.");
    }

    public List<CommentResponseDTO> getCommentBySong(int songId) {
        Song song = this.songRepository.getSongById(songId);
        if (song == null) throw new NotFoundException("Song id#" + songId + " was not found.");
        List<Comment> songComments = this.commentRepository.findCommentsBySong(song);

        return songComments.stream().map(CommentResponseDTO::new).collect(Collectors.toList());
    }

    public CommentResponseDTO getCommentById(int commentId) {
        Comment comment = this.commentRepository.findCommentById(commentId);
        if (comment == null) throw new NotFoundException("Comment id#" + commentId + " was not found.");
        return new CommentResponseDTO(comment);
    }

    @Transactional
    public MessageDTO setLike(int commentId, int likeValue, User loggedUser) {
        Comment targetComment = commentRepository.findCommentById(commentId);
        String action;

        if (targetComment == null) {
            throw new NotFoundException("The comment you are trying to like or dislike was not found.");
        }

        switch (likeValue) {
            case 1:
                if (loggedUser.getLikedComments().contains(targetComment)) return new MessageDTO("Comment left liked.");
                if (loggedUser.getDislikedComments().contains(targetComment)) setLike(commentId, 0, loggedUser);
                loggedUser.getLikedComments().add(targetComment);
                targetComment.getLikers().add(loggedUser);
                action = "liked";
                break;
            case 0:
                if (loggedUser.getLikedComments().contains(targetComment)) {
                    // If comment was previously liked
                    loggedUser.getLikedComments().remove(targetComment);
                    targetComment.getLikers().remove(loggedUser);
                    action = "unliked";
                } else if (loggedUser.getDislikedComments().contains(targetComment)) {
                    // If comment was previously disliked
                    loggedUser.getDislikedComments().remove(targetComment);
                    targetComment.getDislikers().remove(loggedUser);
                    action = "undisliked";
                } else {
                    // If comment was previously neutral
                    return new MessageDTO("Comment status left at neutral.");
                }
                break;
            case -1:
                if (loggedUser.getDislikedComments().contains(targetComment))
                    return new MessageDTO("Comment left disliked.");
                if (loggedUser.getLikedComments().contains(targetComment)) setLike(commentId, 0, loggedUser);
                loggedUser.getDislikedComments().add(targetComment);
                targetComment.getDislikers().add(loggedUser);
                action = "disliked";
                break;
            default:
                throw new BadRequestException("Invalid like status passed.");
        }
        this.userRepository.save(loggedUser);
        this.commentRepository.save(targetComment);
        return new MessageDTO("You successfully " + action + " comment id#" + commentId);
    }

    public CommentResponseDTO editComment(User loggedUser, int commentId, EditCommentRequestDTO requestDTO) {
        Comment comment = this.commentRepository.findCommentById(commentId);

        if (comment == null) throw new NotFoundException("Comment id#" + commentId + " was not found.");
        if (loggedUser.getId() != comment.getOwner().getId()) throw new AuthenticationException("You cannot edit comments from other users.");

        comment.setText(requestDTO.getText());
        this.commentRepository.save(comment);
        return new CommentResponseDTO(comment);
    }
}