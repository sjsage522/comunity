package com.example.comunity.exception;

import com.example.comunity.dto.api.ErrorCode;

public class NotLoginUserException extends BusinessException {

    public NotLoginUserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
