package com.example.comunity.repository;

import com.example.comunity.domain.UploadFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UploadFileRepository {

    private final EntityManager em;

    public List<UploadFile> findAll() {
        return em.createQuery("select f from UploadFile f", UploadFile.class)
                .getResultList();
    }

    public void deleteAllById(final List<Long> ids) {
        em.createQuery(
                "delete from UploadFile f " +
                        " where f.uploadFileId in :ids")
                .setParameter("ids", ids)
                .executeUpdate();
    }
}
