package com.example.comunity.exception;

public class NoMatchBoardInfoException extends RuntimeException {

    public NoMatchBoardInfoException() {
    }

    public NoMatchBoardInfoException(String message) {
        super(message);
    }

    public NoMatchBoardInfoException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoMatchBoardInfoException(Throwable cause) {
        super(cause);
    }
}
