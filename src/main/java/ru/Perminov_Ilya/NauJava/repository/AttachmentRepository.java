package ru.Perminov_Ilya.NauJava.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.Perminov_Ilya.NauJava.entities.Attachment;
import ru.Perminov_Ilya.NauJava.entities.User;
import ru.Perminov_Ilya.NauJava.entities.enums.AttachmentType;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttachmentRepository extends CrudRepository<Attachment, UUID> {
    List<Attachment> findByTypeAndDataSizeBytesBetween(
            AttachmentType type,
            long dataSizeBytesAfter,
            long dataSizeBytesBefore);
}
