package com.example.comunity.dto.board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardUpdateDto extends BoardDto {

    @NotBlank(message = "변경할 제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "변경할 내용을 입력해주세요.")
    private String content;

    @NotBlank(message = "변경할 카테고리를 입력해주세요.")
    private String categoryName;
}
