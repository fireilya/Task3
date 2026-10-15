package ru.Perminov_Ilya.NauJava.entities.enums;

public enum AttachmentType {
    VIDEO(StringValues.VIDEO),
    AUDIO(StringValues.AUDIO),
    PHOTO(StringValues.PHOTO),
    FILE(StringValues.FILE);

    private final String stringValue;

    AttachmentType(final String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public static AttachmentType fromStringValue(String stringValue) {
        for (AttachmentType type : AttachmentType.values()) {
            if (type.stringValue.equals(stringValue))
                return type;
        }
        return null;
    }

    public static class StringValues {
        public static final String VIDEO = "VIDEO";
        public static final String AUDIO = "AUDIO";
        public static final String PHOTO = "PHOTO";
        public static final String FILE = "FILE";
    }
}