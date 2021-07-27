package com.example.comunity.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment")
@SequenceGenerator(
        name = "comment_sequence_generator",
        sequenceName = "comment_sequence"
)
public class Comment extends BaseTimeEntity {

    @Id @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "comment_sequence_generator"
    )
    private Long id;

    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
    private final List<Comment> children = new ArrayList<>();

    public Comment(final User user, final Board board, final String content) {
        if (!(isValidObjects(user, board) && isValidStrings(content))) throw new IllegalArgumentException("유효하지 않은 매개변수 형식 입니다.");
        this.user = user;
        this.board = board;
        this.content = content;
    }

    //====                                ====//
    /**
     * 부모 댓글과 답변 댓글간의 연관관계 편의 메서드
     * @param child 부모댓글의 답글 (계층형)
     */
    public void addChildComment(final Comment child) {
        this.children.add(child);
        child.setParent(this);
    }

    private void setParent(final Comment parent) {
        this.parent = parent;
    }
    //====                                 ====//

    public static Comment of(final User user, final Board board, final String content) {
        return new Comment(user, board, content);
    }

    /**
     * 변경을 위한 추가 메서드 (게시판 정보 수정)
     */
    public void changeContent(final String content) {
        this.content = content;
    }

    private boolean isValidObjects(Object... objects) {
        return Arrays.stream(objects).noneMatch(Objects::isNull);
    }

    private boolean isValidStrings(String... strings) {
        return Arrays.stream(strings).noneMatch(string -> string == null || string.isBlank());
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + id +
                ", user=" + user.getUserId() +
                ", board=" + board.getId() +
                ", content='" + content + '\'' +
                '}';
    }
}
