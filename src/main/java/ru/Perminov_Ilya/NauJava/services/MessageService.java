package ru.Perminov_Ilya.NauJava.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.Perminov_Ilya.NauJava.entities.Attachment;
import ru.Perminov_Ilya.NauJava.entities.Message;
import ru.Perminov_Ilya.NauJava.repository.ChatBaseRepository;
import ru.Perminov_Ilya.NauJava.repository.MessageRepository;
import ru.Perminov_Ilya.NauJava.repository.UserRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService implements IMessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatBaseRepository chatBaseRepository;

    public MessageService(
            MessageRepository messageRepository,
            UserRepository userRepository,
            ChatBaseRepository chatBaseRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.chatBaseRepository = chatBaseRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    public void sendMessage(UUID senderId, UUID chatId, String content, Attachment[] attachments) {
        var sender = userRepository
                .findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        var chat = chatBaseRepository
                .findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setContent(content);
        message.setSentAt(Instant.now());

        if (attachments != null && attachments.length > 0) {
            List<Attachment> attachmentList = new ArrayList<>();
            for (var attachment : attachments) {
                attachment.setMessage(message);
                attachmentList.add(attachment);
            }
            message.setAttachments(attachmentList);
        }

        messageRepository.save(message);
    }
}