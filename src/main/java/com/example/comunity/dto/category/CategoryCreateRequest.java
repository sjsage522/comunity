package com.example.comunity.dto.category;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class CategoryCreateRequest {

    @NotBlank(message = "카테고리 이름을 설정해야 합니다.")
    private String categoryName;
}
