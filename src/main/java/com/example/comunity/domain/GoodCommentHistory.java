package com.example.comunity.domain;

import lombok.Getter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@SequenceGenerator(
        name = "good_comment_history_sequence_generator",
        sequenceName = "good_comment_history_sequence"
)
public class GoodCommentHistory {

    @Id @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "good_comment_history_sequence_generator"
    )
    private Long goodCommentHistoryId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;
}
