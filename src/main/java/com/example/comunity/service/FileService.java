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
        return fileRepository.findById(id)
                .orElseThrow(NoMatchFileInfoException::new);
    }
}
