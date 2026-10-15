package ru.Perminov_Ilya.NauJava.enumsConverters;

import ru.Perminov_Ilya.NauJava.entities.enums.GroupChatMemberRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ChatMemberRoleConverter  implements AttributeConverter<GroupChatMemberRole, String> {
    @Override
    public String convertToDatabaseColumn(GroupChatMemberRole attribute) {
        return attribute.getStringValue();
    }

    @Override
    public GroupChatMemberRole convertToEntityAttribute(String dbData) {
        return GroupChatMemberRole.fromStringValue(dbData);
    }
}
