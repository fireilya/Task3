package ru.Perminov_Ilya.NauJava.enumsConverters;

import ru.Perminov_Ilya.NauJava.entities.enums.ChatType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ChatTypeConverter  implements AttributeConverter<ChatType, String> {
    @Override
    public String convertToDatabaseColumn(ChatType attribute) {
        return attribute.getStringValue();
    }

    @Override
    public ChatType convertToEntityAttribute(String dbData) {
        return ChatType.fromStringValue(dbData);
    }
}
