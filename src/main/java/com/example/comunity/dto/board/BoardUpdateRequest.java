package com.example.comunity.dto.board;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
public class BoardUpdateRequest {

    private String title;

    private String content;

    private String categoryName;
}
