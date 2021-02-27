package com.example.comunity.domain;

import lombok.Getter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@SequenceGenerator(
        name = "upload_file_sequence_generator",
        sequenceName = "upload_file_sequence"
)
public class UploadFile extends BaseTimeEntity {

    @Id @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "upload_file_sequence_generator"
    )
    private Long uploadFileId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    private String originalFileName;
    private String storedFileName;
    private String fileDownLoadUri;
    private String fileType;
    private Long fileSize;

    /**
     * 파일과 게시판간의 연관관계 편의 메서드
     * @param board 파일이 업로드 될 게시판
     */
    public void uploadFile(final Board board) {
        this.board = board;
        board.getUploadFiles().add(this);
    }

    /**
     * 변경을 위한 추가 메서드 (첨부파일 정보 수정)
     */
}
