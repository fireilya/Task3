package ru.Perminov_Ilya.NauJava.criteriaApiRepository;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import ru.Perminov_Ilya.NauJava.entities.Attachment;
import ru.Perminov_Ilya.NauJava.entities.enums.AttachmentType;

import java.util.List;

@Repository
public class AttachmentRepositoryCriteriaApi implements IAttachmentRepositoryCriteriaApi {

    private final EntityManager em;

    public AttachmentRepositoryCriteriaApi(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Attachment> findByTypeAndDataSizeBytesBetween(
            AttachmentType type,
            long dataSizeBytesAfter,
            long dataSizeBytesBefore) {
        var cb = em.getCriteriaBuilder();
        var query = cb.createQuery(Attachment.class);
        var attachment = query.from(Attachment.class);

        query.where(
                cb.equal(attachment.get("type"), type),
                cb.between(attachment.get("dataSizeBytes"), dataSizeBytesAfter, dataSizeBytesBefore)
        );

        return em.createQuery(query).getResultList();
    }
}
