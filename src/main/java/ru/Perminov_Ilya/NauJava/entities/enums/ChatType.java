package ru.Perminov_Ilya.NauJava.entities.enums;

import lombok.Getter;

@Getter
public enum ChatType {
    DIRECT(StringValues.DIRECT, StringValues.USER_FRIENDLY_DIRECT),
    GROUP(StringValues.GROUP, StringValues.USER_FRIENDLY_GROUP);

    private final String stringValue;
    private final String userFriendlyName;

    ChatType(final String stringValue, String userFriendlyName) {
        this.stringValue = stringValue;
        this.userFriendlyName = userFriendlyName;
    }

    public static ChatType fromStringValue(String stringValue) {
        for (ChatType chatType : ChatType.values()) {
            if (chatType.stringValue.equals(stringValue))
                return chatType;
        }
        return null;
    }

    public static class StringValues {
        public static final String DIRECT = "DIRECT";
        public static final String GROUP = "GROUP";

        public static final String USER_FRIENDLY_DIRECT = "Личный";
        public static final String USER_FRIENDLY_GROUP = "Групповой";
    }
}
