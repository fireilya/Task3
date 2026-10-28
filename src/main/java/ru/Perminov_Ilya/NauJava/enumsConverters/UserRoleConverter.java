package ru.Perminov_Ilya.NauJava.enumsConverters;

import ru.Perminov_Ilya.NauJava.entities.enums.UserRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserRoleConverter implements AttributeConverter<UserRole, String> {
    @Override
    public String convertToDatabaseColumn(UserRole attribute) {
        return attribute.getStringValue();
    }

    @Override
    public UserRole convertToEntityAttribute(String dbData) {
        return UserRole.fromStringValue(dbData);
    }
}