package ru.Perminov_Ilya.NauJava.entities.enums;

import lombok.Getter;

@Getter
public enum GroupChatMemberRole {
    MEMBER(StringValues.MEMBER, StringValues.USER_FRIENDLY_MEMBER),
    MODERATOR(StringValues.MODERATOR, StringValues.USER_FRIENDLY_MODERATOR),
    ADMIN(StringValues.ADMIN, StringValues.USER_FRIENDLY_ADMIN);

    private final String stringValue;
    private final String humanFriendlyName;

    GroupChatMemberRole(final String stringValue, String humanFriendlyName) {
        this.stringValue = stringValue;
        this.humanFriendlyName = humanFriendlyName;
    }

    public static GroupChatMemberRole fromStringValue(String stringValue) {
        for (GroupChatMemberRole chatMemberRole : GroupChatMemberRole.values()) {
            if (chatMemberRole.stringValue.equals(stringValue))
                return chatMemberRole;
        }
        return null;
    }

    public static class StringValues {
        public static final String MEMBER = "MEMBER";
        public static final String MODERATOR = "MODERATOR";
        public static final String ADMIN = "ADMIN";

        public static final String USER_FRIENDLY_MEMBER = "Участник";
        public static final String USER_FRIENDLY_MODERATOR = "Модератор";
        public static final String USER_FRIENDLY_ADMIN = "Администратор";
    }
}

