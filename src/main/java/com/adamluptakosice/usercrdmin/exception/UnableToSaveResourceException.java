package com.adamluptakosice.usercrdmin.exception;

public class UnableToSaveResourceException extends RuntimeException{

    public UnableToSaveResourceException(String message) {
        super(message);
    }

    public UnableToSaveResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
