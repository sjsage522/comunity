package com.example.comunity.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@SequenceGenerator(
        name = "board_sequence_generator",
        sequenceName = "board_sequence"
)
public class Board extends BaseTimeEntity {

    @Id @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "board_sequence_generator")
    private Long boardId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private String title;
    private String content;

//    private String boardUri;

    @OneToMany(mappedBy = "board")
    private final List<UploadFile> uploadFiles = new ArrayList<>();

    @OneToMany(mappedBy = "board")
    private final List<Comment> comments = new ArrayList<>();

    private Board(final User user, final Category category, final String title, final String content) {
        this.user = user;
        this.category = category;
        this.title = title;
        this.content = content;
    }

    public void changeTitle(final String title) {
        this.title = title;
    }

    public void changeContent(final String content) {
        this.content = content;
    }

    /**
     * 게시판과 파일간의 연관관계 편의 메서드
     * @param uploadFiles 해당 게시판에 업로드될 파일들
     */
    public void uploadFiles(final List<UploadFile> uploadFiles) {
        for (UploadFile file : uploadFiles) {
            if (!this.uploadFiles.contains(file)) file.uploadFile(this);
        }
    }

    /**
     * 게시판과 카테고리간의 연관관계 편의 메서드
     * @param category 해당 게시판이 포함되는 카테고리
     */
    public void changeCategory(final Category category) {
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
    public static Board createBoard(final User user, final Category category, final String title, final String content) {
        return new Board(user, category, title, content);
    }

    /**
     * 변경을 위한 추가 메서드 (게시판 정보 수정)
     */
//    public void modifyBoard(final Category category, final String title, final String content, final UploadFile... uploadFiles) {
//        this.category = category;
//        this.title = title;
//        this.content = content;
//        this.uploadFiles = Arrays.asList(uploadFiles);
//    }
}
