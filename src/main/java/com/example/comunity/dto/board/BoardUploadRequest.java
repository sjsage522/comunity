package com.example.comunity.dto.board;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class BoardUploadRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "글내용을 입력해주세요.")
    private String content;

    @NotBlank(message = "카테고리를 설정해주세요.")
    private String categoryName;
}
