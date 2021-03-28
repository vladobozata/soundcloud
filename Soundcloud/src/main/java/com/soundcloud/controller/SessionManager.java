package com.soundcloud.controller;

import com.soundcloud.exceptions.BadRequestException;
import com.soundcloud.model.POJOs.User;
import com.soundcloud.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Component
public class SessionManager {
    private static final String LOGGED_USER_ID = "LOGGED_USER_ID";
    private final UserRepository userRepository;

    @Autowired
    public SessionManager(UserRepository repository) {
        this.userRepository = repository;
    }

    public User getLoggedUser(HttpSession session) {
        if (session.getAttribute(LOGGED_USER_ID) == null) {
            return null;
        }
        int userId = (int) session.getAttribute(LOGGED_USER_ID);
        return this.userRepository.findUserById(userId);
    }

    public User validateUser(HttpSession session, String message){
        User loggedUser = getLoggedUser(session);
        if(loggedUser == null){
            throw new BadRequestException(message);
        }
        return loggedUser;
    }

    public void loginUser(HttpSession ses, int id) {
        ses.setAttribute(LOGGED_USER_ID, id);
    }

    public void logoutUser(HttpSession ses) {
        ses.invalidate();
    }
}