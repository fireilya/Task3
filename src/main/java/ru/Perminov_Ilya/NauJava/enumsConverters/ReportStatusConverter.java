package ru.Perminov_Ilya.NauJava.enumsConverters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.Perminov_Ilya.NauJava.entities.enums.ChatType;
import ru.Perminov_Ilya.NauJava.entities.enums.ReportStatus;

@Converter(autoApply = true)
public class ReportStatusConverter implements AttributeConverter<ReportStatus, String> {
    @Override
    public String convertToDatabaseColumn(ReportStatus attribute) {
        return attribute.getStringValue();
    }

    @Override
    public ReportStatus convertToEntityAttribute(String dbData) {
        return ReportStatus.fromStringValue(dbData);
    }
}
