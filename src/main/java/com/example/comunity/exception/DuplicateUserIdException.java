package com.example.comunity.exception;

public class DuplicateUserIdException extends RuntimeException {

    public DuplicateUserIdException() {
    }

    public DuplicateUserIdException(String message) {
        super(message);
    }

    public DuplicateUserIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateUserIdException(Throwable cause) {
        super(cause);
    }
}
