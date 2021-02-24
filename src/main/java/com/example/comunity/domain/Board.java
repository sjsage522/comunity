package com.example.comunity.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
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

    @OneToMany(mappedBy = "board")
    private List<UploadFile> uploadFiles;


    public Board(User user, Category category, String title, String content) {
        this.user = user;
        this.category = category;
        this.title = title;
        this.content = content;
    }

    /**
     * 게시판과 파일간의 연관관계 편의 메서드
     * @param uploadFiles 해당 게시판에 업로드될 파일들
     */
    public void uploadFiles(UploadFile... uploadFiles) {
        for (UploadFile file : uploadFiles) {
            if (!this.uploadFiles.contains(file)) file.uploadFile(this);
        }
    }

    /**
     * 게시판과 카테고리간의 연관관계 편의 메서드
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
}
