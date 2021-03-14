package com.example.comunity.service;

import com.example.comunity.domain.UploadFile;
import com.example.comunity.exception.NoMatchFileInfoException;
import com.example.comunity.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    public UploadFile findById(final Long id) {
        UploadFile findFile = fileRepository.findById(id);
        if (findFile == null) throw new NoMatchFileInfoException("존재하지 않는 파일입니다.");
        return findFile;
    }
}
