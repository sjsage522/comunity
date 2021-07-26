package com.example.comunity.exception;

import com.example.comunity.dto.api.ErrorCode;

public class DuplicateCategoryNameException extends BusinessException {

    public DuplicateCategoryNameException() {
        super(ErrorCode.DUPLICATE_CATEGORY_NAME);
    }
}
