package com.example.comunity.dto.board;

import com.example.comunity.domain.UploadFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardUploadDto extends BoardDto {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "글내용을 입력해주세요.")
    private String content;

    @NotBlank(message = "카테고리를 설정해주세요.")
    private String categoryName;

    private List<UploadFile> uploadFiles;
}
