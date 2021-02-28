package com.example.comunity.exception;

public class UserFormatException extends RuntimeException {
    public UserFormatException() {
    }

    public UserFormatException(String message) {
        super(message);
    }

    public UserFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserFormatException(Throwable cause) {
        super(cause);
    }
}
