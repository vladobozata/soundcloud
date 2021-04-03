package com.soundcloud.controller;

import com.soundcloud.exceptions.AuthenticationException;
import com.soundcloud.exceptions.BadRequestException;
import com.soundcloud.exceptions.FileReadWriteException;
import com.soundcloud.exceptions.NotFoundException;
import com.soundcloud.model.DTOs.MessageDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.sql.SQLException;

public abstract class AbstractController {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MessageDTO handleBadRequest(BadRequestException e){
        return new MessageDTO(e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public MessageDTO handleNotAuthorized(AuthenticationException e){
        return new MessageDTO(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public MessageDTO handleNotFoundResource(NotFoundException e){
        return new MessageDTO(e.getMessage());
    }

    @ExceptionHandler(FileReadWriteException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public MessageDTO handleFileException(FileReadWriteException e) {
        return new MessageDTO(e.getMessage());
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public MessageDTO handleSQLException(SQLException e) {
        return new MessageDTO("Unable to retrieve results from the database. " + e.getMessage());
    }
}