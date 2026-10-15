package ru.Perminov_Ilya.NauJava.entities.enums;

public enum GroupChatMemberRole {
    MEMBER(StringValues.MEMBER),
    MODERATOR(StringValues.MODERATOR),
    ADMIN(StringValues.ADMIN);

    private final String stringValue;

    GroupChatMemberRole(final String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
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
    }
}

