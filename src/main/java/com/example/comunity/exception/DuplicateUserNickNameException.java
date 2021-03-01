package com.example.comunity.exception;

public class DuplicateUserNickNameException extends RuntimeException {

    public DuplicateUserNickNameException() {
    }

    public DuplicateUserNickNameException(final String message) {
        super(message);
    }

    public DuplicateUserNickNameException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DuplicateUserNickNameException(final Throwable cause) {
        super(cause);
    }
}
