package com.example.comunity.exception;

import com.example.comunity.dto.api.ErrorCode;

public class DuplicateUserIdException extends BusinessException {

    public DuplicateUserIdException() {
        super(ErrorCode.DUPLICATE_USER_ID);
    }
}
