package ru.Perminov_Ilya.NauJava.entities.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN(StringValues.ADMIN),
    USER(StringValues.USER);

    private final String stringValue;

    UserRole(final String stringValue) {
        this.stringValue = stringValue;
    }

    public static UserRole fromStringValue(String stringValue) {
        for (UserRole role : UserRole.values()) {
            if (role.stringValue.equals(stringValue))
                return role;
        }
        return null;
    }

    public static class StringValues {
        public static final String ADMIN = "ADMIN";
        public static final String USER = "USER";
    }
}