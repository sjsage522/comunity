package com.example.comunity.exception;

public class DuplicateUserIdException extends RuntimeException {

    public DuplicateUserIdException() {
    }

    public DuplicateUserIdException(final String message) {
        super(message);
    }

    public DuplicateUserIdException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DuplicateUserIdException(final Throwable cause) {
        super(cause);
    }
}
