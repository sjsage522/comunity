package com.example.comunity.exception;

import com.example.comunity.dto.api.ErrorCode;

public class AccessDeniedException extends BusinessException {

    public AccessDeniedException() {
        super(ErrorCode.FORBIDDEN);
    }
}
