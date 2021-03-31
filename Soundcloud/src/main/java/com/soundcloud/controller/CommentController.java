package com.soundcloud.controller;

import com.soundcloud.model.DTOs.Comment.PostCommentRequestDTO;
import com.soundcloud.model.DTOs.MessageDTO;
import com.soundcloud.model.DTOs.ResourceRequestDTO;
import com.soundcloud.model.POJOs.User;
import com.soundcloud.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class CommentController extends AbstractController {

    private final SessionManager sessionManager;
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService service, SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.commentService = service;
    }

    // POST
    @PostMapping("/comments")
    public MessageDTO postComment(@RequestBody PostCommentRequestDTO requestDTO, HttpSession session) {
        User loggedUser = sessionManager.validateUser(session, "You must login before posting a comment.");
        return commentService.postComment(requestDTO, loggedUser);
    }

    // DELETE
    @DeleteMapping("/comments")
    public MessageDTO deleteComment(@RequestBody ResourceRequestDTO requestDTO, HttpSession session) {
        User loggedUser = sessionManager.validateUser(session, "You must login in order to delete a comment.");
        return commentService.deleteComment(requestDTO, loggedUser);
    }
}