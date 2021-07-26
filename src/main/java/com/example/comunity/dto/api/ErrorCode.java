package com.example.comunity.dto.api;

import lombok.Getter;

@Getter
public enum ErrorCode {

    DUPLICATE_USER_ID(400, "이미 존재하는 사용자 ID 입니다."),
    DUPLICATE_USER_NICKNAME(400, "이미 존재하는 사용자 NICKNAME 입니다."),
    DUPLICATE_CATEGORY_NAME(400, "이미 존재하는 카테고리명 입니다."),
    NOMATCH_BOARD_INFO(400, "존재하지 않는 게시글 입니다."),
    NOMATCH_CATEGORY_INFO(400, "존재하지 않는 카테고리 입니다."),
    NOMATCH_COMMENT_INFO(400, "존재하지 않는 답글 입니다."),
    NOMATCH_FILE_INFO(400, "존재하지 않는 파일 입니다."),
    NOMATCH_USER_INFO(400, "존재하지 않는 사용자 입니다."),
    INCORRECT_FORMAT(400, "잘못된 형식입니다."),

    HANDLER_NOT_FOUND(404, "요청 주소가 잘못되었습니다."),

    METHOD_NOT_ALLOWED(405, "지원하는 않는 요청 메서드 입니다."),

    INTERNAL_SERVER_ERROR(500, "내부 서버 에러.");

    private final int status;
    private final String message;

    ErrorCode(final int status, final String message) {
        this.status = status;
        this.message = message;
    }
}
