package com.soundcloud.exceptions;

public class FileReadWriteException extends RuntimeException {
    public FileReadWriteException(String message) {
        super(message);
    }
}
