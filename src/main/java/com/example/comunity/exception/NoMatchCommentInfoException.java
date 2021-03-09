package com.example.comunity.exception;

public class NoMatchCommentInfoException extends RuntimeException {

    public NoMatchCommentInfoException() {
    }

    public NoMatchCommentInfoException(String message) {
        super(message);
    }

    public NoMatchCommentInfoException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoMatchCommentInfoException(Throwable cause) {
        super(cause);
    }
}
