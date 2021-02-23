package com.example.comunity.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {

    @Id @GeneratedValue
    private Long boardId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private String title;
    private String content;

    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime modifiedDate;
    private String boardUri;
    //private List<UploadFile> uploadFiles;


    public Board(User user, Category category, String title, String content) {
        this.user = user;
        this.category = category;
        this.title = title;
        this.content = content;
    }

    /**
     * 게시판과 카테고리 연관관계 편의 메서드
     * @param category 해당 게시판이 포함되는 카테고리
     */
    public void changeCategory(Category category) {
        this.category = category;

        List<Board> boards = category.getBoards();
        if (!boards.contains(this))
            category.getBoards().add(this);
    }


    /**
     * 게시판 생성
     * @param user 게시판글을 작성한 사용자 엔티티
     * @param category 해당 게시판이 포함되는 카테고리
     * @param title 게시판의 제목
     * @param content 게시판 글 내용
     */
    public static Board createBoard(User user, Category category, String title, String content) {
        return new Board(user, category, title, content);
    }

    /**
     * 추후 첨부파일을 포함하여 구현
     * @param user
     * @param category
     * @param title
     * @param content
     * @param boardUri
     */
    public void createBoard(User user, Category category, String title, String content, String boardUri) {

    }
}
