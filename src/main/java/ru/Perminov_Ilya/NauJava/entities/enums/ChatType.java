package ru.Perminov_Ilya.NauJava.entities.enums;

public enum ChatType {
    DIRECT(StringValues.DIRECT),
    GROUP(StringValues.GROUP);

    private final String stringValue;

    ChatType(final String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
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
    }
}
