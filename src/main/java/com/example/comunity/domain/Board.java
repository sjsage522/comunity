package com.example.comunity.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "board")
@SequenceGenerator(
        name = "board_sequence_generator",
        sequenceName = "board_sequence"
)
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "board_sequence_generator")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private final List<UploadFile> uploadFiles = new ArrayList<>();

    private Board(final User user, final Category category, final String title, final String content) {
        if (!(isValidObjects(user, category) && isValidStrings(title, content)))
            throw new IllegalArgumentException("유효하지 않은 매개변수 형식 입니다.");
        this.user = user;
        this.category = category;
        this.title = title;
        this.content = content;
    }

    /**
     * 게시판과 파일간의 연관관계 편의 메서드
     *
     * @param uploadFiles 해당 게시판에 업로드될 파일들
     */
    public void uploadFiles(final List<UploadFile> uploadFiles) {
        for (UploadFile file : uploadFiles) {
            if (!this.uploadFiles.contains(file)) file.uploadFile(this);
        }
    }

    // 변경을 위한 추가 메서드 (게시판 정보 수정)
    public void changeBoard(final String title, final String content, final Category changedCategory) {
        this.title = (title == null || title.isBlank()) ? this.title : title;
        this.content = (content == null || content.isBlank()) ? this.content : content;
        this.category = (changedCategory == null) ? this.category : changedCategory;
    }

    /**
     * 게시판 생성
     *
     * @param user     게시판글을 작성한 사용자 엔티티
     * @param category 해당 게시판이 포함되는 카테고리
     * @param title    게시판의 제목
     * @param content  게시판 글 내용
     */
    public static Board of(final User user, final Category category, final String title, final String content) {
        return new Board(user, category, title, content);
    }

    private boolean isValidObjects(Object... objects) {
        return Arrays.stream(objects).noneMatch(Objects::isNull);
    }

    private boolean isValidStrings(String... strings) {
        return Arrays.stream(strings).noneMatch(string -> string == null || string.isBlank());
    }

    @Override
    public String toString() {
        return "Board{" +
                "boardId=" + id +
                ", user=" + user.getUserId() +
                ", category=" + category.getCategoryName() +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", uploadFiles(size)=" + uploadFiles.size() +
                '}';
    }
}
