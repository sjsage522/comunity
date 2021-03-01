package com.example.comunity.exception;

public class NoMatchUserInfoException extends RuntimeException {

    public NoMatchUserInfoException() {
    }

    public NoMatchUserInfoException(final String message) {
        super(message);
    }

    public NoMatchUserInfoException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public NoMatchUserInfoException(final Throwable cause) {
        super(cause);
    }
}
