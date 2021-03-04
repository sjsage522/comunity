package com.example.comunity.exception;

public class NoMatchCategoryInfoException extends RuntimeException {

    public NoMatchCategoryInfoException() {
    }

    public NoMatchCategoryInfoException(String message) {
        super(message);
    }

    public NoMatchCategoryInfoException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoMatchCategoryInfoException(Throwable cause) {
        super(cause);
    }
}
