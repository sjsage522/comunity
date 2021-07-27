package com.example.comunity.controller;

import com.example.comunity.domain.UploadFile;
import com.example.comunity.service.FileService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
        try {
            final byte[] data = FileUtils.readFileToByteArray(file);
            response.setContentType("application/octet-stream");
            response.setContentLength(data.length);
            response.setHeader("Content-Transfer-Encoding", "binary");
            response.setHeader("Content-Disposition", "attachment; fileName=\""
                    + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + "\";");

            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(data);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("파일 다운로드에 실패하였습니다.");
        }
    }
}
