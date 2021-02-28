package com.example.comunity.exception;

public class NoMatchUserInfoException extends RuntimeException {

    public NoMatchUserInfoException() {
    }

    public NoMatchUserInfoException(String message) {
        super(message);
    }

    public NoMatchUserInfoException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoMatchUserInfoException(Throwable cause) {
        super(cause);
    }
}
