package com.example.comunity.dto.board;

import lombok.Setter;
import org.springframework.util.Assert;

import javax.validation.constraints.NotBlank;
import java.util.Arrays;

@Setter
public class BoardUploadRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "글내용을 입력해주세요.")
    private String content;

    @NotBlank(message = "카테고리를 설정해주세요.")
    private String categoryName;

    protected BoardUploadRequest() {}

    private BoardUploadRequest(final String title, final String content, final String categoryName) {
        Assert.noNullElements(Arrays.asList(title, content, categoryName), "arguments must not be null.");
        this.title = title;
        this.content = content;
        this.categoryName = categoryName.toUpperCase();
    }

    public static BoardUploadRequest of(final String title, final String content, final String categoryName) {
        return new BoardUploadRequest(title, content, categoryName);
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getCategoryName() {
        return categoryName.toUpperCase();
    }

    @Override
    public String toString() {
        return "BoardUploadRequest{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}
