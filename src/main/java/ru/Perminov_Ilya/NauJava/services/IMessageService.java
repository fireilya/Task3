package ru.Perminov_Ilya.NauJava.services;

import ru.Perminov_Ilya.NauJava.entities.Attachment;
import ru.Perminov_Ilya.NauJava.entities.Message;

import java.util.UUID;

public interface IMessageService {
    void sendMessage(UUID senderId, UUID chatId, String content, final Attachment[] attachments);
}
