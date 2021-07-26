package com.example.comunity.dto.api;

import lombok.Getter;
import org.springframework.validation.BindingResult;

@Getter
public class ErrorResponse {
    private final String message;
    private final int status;

    private ErrorResponse(final ErrorCode errorCode) {
        this.message = errorCode.getMessage();
        this.status = errorCode.getStatus();
    }

    private ErrorResponse(final String message, final int status) {
        this.message = message;
        this.status = status;
    }

    public static ErrorResponse from(final ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }

    public static ErrorResponse of(final BindingResult bindingResult, final ErrorCode errorCode) {
        final String message = bindingResult
                .getAllErrors()
                .get(0)
                .getDefaultMessage();
        final int status = errorCode.getStatus();

        return new ErrorResponse(message, status);
    }
}
