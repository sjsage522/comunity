package com.example.comunity.exception;

import com.example.comunity.dto.api.ErrorCode;

public class NoMatchBoardInfoException extends BusinessException {

    public NoMatchBoardInfoException() {
        super(ErrorCode.NOMATCH_BOARD_INFO);
    }
}
