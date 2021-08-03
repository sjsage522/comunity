package com.example.comunity.exception;

import com.example.comunity.dto.api.ErrorCode;

public class NoMatchUserInfoException extends BusinessException {

    public NoMatchUserInfoException() {
        super(ErrorCode.NOMATCH_USER_INFO);
    }

    public NoMatchUserInfoException(final String message) {
        super(message, ErrorCode.NOMATCH_USER_INFO);
    }
}
