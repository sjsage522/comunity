package com.example.comunity.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "upload_file")
@SequenceGenerator(
        name = "upload_file_sequence_generator",
        sequenceName = "upload_file_sequence"
)
public class UploadFile extends BaseTimeEntity {

    @Id @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "upload_file_sequence_generator"
    )
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "stored_file_name", nullable = false)
    private String storedFileName;

    @Column(name = "file_download_uri", nullable = false)
    private String fileDownloadUri;

    @Column(name = "file_type", nullable = false)
    private String fileType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    private UploadFile(final Board board, final String originalFileName, final String storedFileName, final Long fileSize,
                       final String fileDownloadUri, final String fileType) {
        uploadFile(board);
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.fileSize = fileSize;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
    }

    /**
     * 파일과 게시판간의 연관관계 편의 메서드
     * @param board 파일이 업로드 될 게시판
     */
    public void uploadFile(final Board board) {
        this.board = board;
        if (board.getUploadFiles() != null)
            board.getUploadFiles().add(this);
    }

    public static UploadFile from(final Board board, final String originalFileName, final String storedFileName, final Long fileSize,
                                  final String fileDownLoadUri, final String fileType) {
        return new UploadFile(board, originalFileName, storedFileName, fileSize, fileDownLoadUri, fileType);
    }

    @Override
    public String toString() {
        return "UploadFile{" +
                "uploadFileId=" + id +
                ", board=" + board.getTitle() +
                ", originalFileName='" + originalFileName + '\'' +
                ", storedFileName='" + storedFileName + '\'' +
                ", fileDownloadUri='" + fileDownloadUri + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                '}';
    }
}
