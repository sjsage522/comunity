package com.example.comunity.controller.api;

import com.example.comunity.domain.UploadFile;
import com.example.comunity.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * 파일 다운로드 api
     *
     * @param fileId   다운로드할 파일 아이디
     * @param response 파일 응답 dto
     */
    @GetMapping("/download/{fileId}")
    public void fileDownload(
            final @PathVariable("fileId") Long fileId,
            final HttpServletResponse response) {
        final UploadFile findFile = fileService.findById(fileId);

        final String uploadPath = Paths.get(findFile.getFileDownloadUri()).toString();
        final String fileName = findFile.getOriginalFileName();

        final File file = new File(uploadPath, findFile.getStoredFileName());
        try (FileInputStream fis = new FileInputStream(uploadPath + "/" + findFile.getStoredFileName());
             OutputStream out = response.getOutputStream()) {
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + "\";");
            response.setHeader("Content-Transfer-Encoding", "binary");
            response.setHeader("Content-Length", "" + file.length());

            FileCopyUtils.copy(fis, out);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("파일 다운로드에 실패하였습니다.");
        }
    }
}
