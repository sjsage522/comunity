package com.example.comunity.exception;

import com.example.comunity.dto.api.ErrorCode;

public class DuplicateUserNickNameException extends BusinessException {

    public DuplicateUserNickNameException() {
        super(ErrorCode.DUPLICATE_USER_NICKNAME);
    }
}
