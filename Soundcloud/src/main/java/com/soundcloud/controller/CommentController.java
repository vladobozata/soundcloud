package com.soundcloud.controller;

import com.soundcloud.service.CommentService;
import com.soundcloud.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommentController extends AbstractController {

    private final SessionManager sessionManager;
    private final CommentService service;

    @Autowired
    public CommentController(CommentService service, SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.service = service;
    }

}