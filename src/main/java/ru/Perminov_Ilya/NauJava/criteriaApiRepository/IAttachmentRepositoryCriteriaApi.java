package ru.Perminov_Ilya.NauJava.criteriaApiRepository;

import ru.Perminov_Ilya.NauJava.entities.Attachment;
import ru.Perminov_Ilya.NauJava.entities.enums.AttachmentType;

import java.util.List;

public interface IAttachmentRepositoryCriteriaApi {
    List<Attachment> findByTypeAndDataSizeBytesBetween(
            AttachmentType type,
            long dataSizeBytesAfter,
            long dataSizeBytesBefore);
}
