package com.example.comunity.exception;

public class NoMatchFileInfoException extends RuntimeException {

    public NoMatchFileInfoException() {
    }

    public NoMatchFileInfoException(String message) {
        super(message);
    }

    public NoMatchFileInfoException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoMatchFileInfoException(Throwable cause) {
        super(cause);
    }
}
