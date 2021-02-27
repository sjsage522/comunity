package com.example.comunity.domain;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@SequenceGenerator(
        name = "comment_sequence_generator",
        sequenceName = "comment_sequence"
)
public class Comment extends BaseTimeEntity {

    @Id @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "comment_sequence_generator"
    )
    private Long commentId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    private String content;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent")
    private final List<Comment> children = new ArrayList<>();

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

    /**
     * 변경을 위한 추가 메서드 (게시판 정보 수정)
     */
}
