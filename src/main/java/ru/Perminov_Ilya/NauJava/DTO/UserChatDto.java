package ru.Perminov_Ilya.NauJava.DTO;

import java.time.LocalDateTime;

public record UserChatDto(
        String title,
        String tag,
        String type,
        String role,
        LocalDateTime createdAt
){}
