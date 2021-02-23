package com.example.comunity.domain;

import lombok.Getter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
public class UploadFile {

    @Id @GeneratedValue
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
    public void uploadFile(Board board) {
        this.board = board;
        board.getUploadFiles().add(this);
    }
}
