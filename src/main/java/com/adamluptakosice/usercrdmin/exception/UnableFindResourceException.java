package com.adamluptakosice.usercrdmin.exception;

public class UnableFindResourceException extends RuntimeException {
    public UnableFindResourceException(String message) {
        super(message);
    }

    public UnableFindResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
