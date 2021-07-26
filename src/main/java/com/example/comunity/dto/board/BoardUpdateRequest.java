package com.example.comunity.dto.board;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
public class BoardUpdateRequest {

    @NotBlank(message = "변경할 제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "변경할 내용을 입력해주세요.")
    private String content;

    @NotBlank(message = "변경할 카테고리를 입력해주세요.")
    private String categoryName;
}
