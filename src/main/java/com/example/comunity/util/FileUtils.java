package com.example.comunity.util;

import com.example.comunity.domain.Board;
import com.example.comunity.dto.file.UploadFileDto;
import com.example.comunity.exception.UploadFileException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class FileUtils {

    private final String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
    private final String uploadPath = Paths.get("/Users", "jun", "Development", "downloads", today).toString();


    /**
     * @return 서버에 저장될 파일이름 (랜덤 문자열)
     */
    private String getRandomString() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public List<UploadFileDto> uploadFiles(final MultipartFile[] files, final Board board) {

        /* 파일이 비어있는 경우 */
        if (files == null || files[0].getSize() < 1) {
            return Collections.emptyList();
        }

        List<UploadFileDto> fileList = new ArrayList<>();

        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        for (MultipartFile file : files) {
            try {
                /* 파일 확장자 */
                final String extension = FilenameUtils.getExtension(file.getOriginalFilename());

                /* 서버에 저장할 파일명 */
                final String saveName = getRandomString() + "." + extension;

                /* 업로드 경로에 saveName 과 동일한 이름을 갖는 파일 생성 */
                File target = new File(uploadPath, saveName);
                file.transferTo(target);

                /* 파일 정보 저장 */
                UploadFileDto uploadFileDto = new UploadFileDto();
                uploadFileDto.setBoardId(board.getBoardId());
                uploadFileDto.setOriginalFileName(file.getOriginalFilename());
                uploadFileDto.setStoredFileName(saveName);
                uploadFileDto.setFileSize(file.getSize());

                fileList.add(uploadFileDto);

            } catch (IOException ioe) {
                throw new UploadFileException("[ " + file.getOriginalFilename() + " ] 파일 저장을 실패했습니다.");
            } catch (Exception e) {
                throw new UploadFileException("[ " + file.getOriginalFilename() + " ] 알 수 없는 오류가 발생했습니다.");
            }
        }

        return fileList;
    }
}
