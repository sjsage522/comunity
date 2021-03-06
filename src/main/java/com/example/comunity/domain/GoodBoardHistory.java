package com.example.comunity.domain;

import lombok.Getter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@SequenceGenerator(
        name = "good_board_history_sequence_generator",
        sequenceName = "good_board_history_sequence"
)
public class GoodBoardHistory {

    @Id @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "good_board_history_sequence_generator"
    )
    private Long goodBoardHistoryId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board board;
}
