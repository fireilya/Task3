package ru.Perminov_Ilya.NauJava.entities.enums;

import lombok.Getter;

@Getter
public enum ReportStatus {
    CREATED(StringValues.CREATED, StringValues.USER_FRIENDLY_CREATED),
    READY(StringValues.READY, StringValues.USER_FRIENDLY_READY),
    ERROR(StringValues.ERROR, StringValues.USER_FRIENDLY_ERROR);

    private final String stringValue;
    private final String userFriendlyName;

    ReportStatus(String stringValue, String userFriendlyName) {
        this.stringValue = stringValue;
        this.userFriendlyName = userFriendlyName;
    }

    public static ReportStatus fromStringValue(String stringValue) {
        for (ReportStatus role : ReportStatus.values()) {
            if (role.stringValue.equals(stringValue))
                return role;
        }
        return null;
    }

    private static class StringValues {
        public static final String CREATED = "CREATED";
        public static final String READY = "READY";
        public static final String ERROR = "ERROR";

        public static final String USER_FRIENDLY_CREATED = "Создан";
        public static final String USER_FRIENDLY_READY = "Завершён";
        public static final String USER_FRIENDLY_ERROR = "Ошибка";
    }
}
