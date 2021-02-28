package com.example.comunity.exception;

public class DuplicateUserNickNameException extends RuntimeException {

    public DuplicateUserNickNameException() {
    }

    public DuplicateUserNickNameException(String message) {
        super(message);
    }

    public DuplicateUserNickNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateUserNickNameException(Throwable cause) {
        super(cause);
    }
}
