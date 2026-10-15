package ru.Perminov_Ilya.NauJava.enumsConverters;

import ru.Perminov_Ilya.NauJava.entities.enums.AttachmentType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AttachmentTypeConverter implements AttributeConverter<AttachmentType, String> {
    @Override
    public String convertToDatabaseColumn(AttachmentType attribute) {
        return attribute.getStringValue();
    }

    @Override
    public AttachmentType convertToEntityAttribute(String dbData) {
        return AttachmentType.fromStringValue(dbData);
    }
}