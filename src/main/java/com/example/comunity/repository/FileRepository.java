package com.example.comunity.repository;

import com.example.comunity.domain.UploadFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FileRepository {

    private static final int BATCH_SIZE = 5;
    private final EntityManager em;

    public int uploadFiles(final List<UploadFile> fileList) {
        int size = fileList.size();
        for (int i = 0; i < size; i++) {
            if (i > 0 && i % BATCH_SIZE == 0) {
                em.flush();
                em.clear();
            }
            em.persist(fileList.get(i));
        }
        em.flush();
        return size;
    }

    public UploadFile findFileById(final Long fileId) {
        return em.find(UploadFile.class, fileId);
    }

    public int deleteFilesByBoardId(final Long boardId) {
        return em.createQuery(
                "delete from UploadFile f " +
                        " where f.board.boardId = :boardId")
                .setParameter("boardId", boardId)
                .executeUpdate();
    }

    public List<UploadFile> findAll(final Long boardId) {
        return em.createQuery(
                "select f from UploadFile f" +
                        " where f.board.boardId = :boardId", UploadFile.class)
                .setParameter("boardId", boardId)
                .getResultList();
    }

    public Long findCount(final Long boardId) {
        return (Long) em.createQuery(
                "select count(f) from UploadFile f" +
                        " where f.board.boardId = :boardId")
                .setParameter("boardId", boardId)
                .getSingleResult();
    }
}
