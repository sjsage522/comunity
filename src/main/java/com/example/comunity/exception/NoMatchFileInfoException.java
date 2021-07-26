package com.example.comunity.exception;

import com.example.comunity.dto.api.ErrorCode;

public class NoMatchFileInfoException extends BusinessException {

    public NoMatchFileInfoException() {
        super(ErrorCode.NOMATCH_FILE_INFO);
    }
}
