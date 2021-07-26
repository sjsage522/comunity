package com.example.comunity.exception;

import com.example.comunity.dto.api.ErrorCode;

public class NoMatchCommentInfoException extends BusinessException {

    public NoMatchCommentInfoException() {
        super(ErrorCode.NOMATCH_COMMENT_INFO);
    }
}
