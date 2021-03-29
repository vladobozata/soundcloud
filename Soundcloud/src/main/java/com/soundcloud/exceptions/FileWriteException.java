package com.soundcloud.exceptions;

public class FileWriteException extends RuntimeException {
    public FileWriteException(String message) {
        super(message);
    }
}
