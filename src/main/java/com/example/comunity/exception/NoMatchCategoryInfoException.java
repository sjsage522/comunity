package com.example.comunity.exception;

import com.example.comunity.dto.api.ErrorCode;

public class NoMatchCategoryInfoException extends BusinessException {

    public NoMatchCategoryInfoException() {
        super(ErrorCode.NOMATCH_CATEGORY_INFO);
    }
}
