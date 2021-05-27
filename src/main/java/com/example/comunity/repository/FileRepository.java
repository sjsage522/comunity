package com.example.comunity.repository;

import com.example.comunity.domain.UploadFile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FileRepository extends JpaRepository<UploadFile, Long> {

    @EntityGraph(attributePaths = "board")
    List<UploadFile> findAllByBoard_BoardId(Long boarId);

    @Modifying
    @Query("delete from UploadFile f where f.uploadFileId in :ids")
    void deleteWithIds(List<Long> ids);
}
