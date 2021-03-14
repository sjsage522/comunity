package com.example.comunity.controller;

import com.example.comunity.domain.UploadFile;
import com.example.comunity.service.FileService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/download/{fileId}")
    public void fileDownload(@PathVariable("fileId") final Long fileId,
                                                 final HttpServletResponse response) {
        UploadFile findFile = fileService.findById(fileId);

        String uploadPath = Paths.get(findFile.getFileDownloadUri()).toString();
        String fileName = findFile.getOriginalFileName();

        File file = new File(uploadPath, findFile.getStoredFileName());
        try {
            byte[] data = FileUtils.readFileToByteArray(file);
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
