package com.example.comunity.exception;

public class UserFormatException extends RuntimeException {
    public UserFormatException() {
    }

    public UserFormatException(final String message) {
        super(message);
    }

    public UserFormatException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UserFormatException(final Throwable cause) {
        super(cause);
    }
}
