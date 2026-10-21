package ru.Perminov_Ilya.NauJava.controllers;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.web.bind.annotation.*;
import ru.Perminov_Ilya.NauJava.entities.Attachment;
import ru.Perminov_Ilya.NauJava.entities.Message;
import ru.Perminov_Ilya.NauJava.entities.enums.AttachmentType;
import ru.Perminov_Ilya.NauJava.repository.*;

import java.security.InvalidParameterException;
import java.util.UUID;

@RestController
@RequestMapping("/crmw")
public class CustomRepositoryMethodsWorkController {
    private final AttachmentRepository attachmentRepository;
    public final DirectChatRepository directChatRepository;
    public final MessageRepository messageRepository;
    public final UserRepository userRepository;
    private final ChatBaseRepository chatBaseRepository;

    public CustomRepositoryMethodsWorkController(
            AttachmentRepository attachmentRepository,
            DirectChatRepository directChatRepository,
            MessageRepository messageRepository,
            UserRepository userRepository,
            ChatBaseRepository chatBaseRepository) {
        this.attachmentRepository = attachmentRepository;
        this.directChatRepository = directChatRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.chatBaseRepository = chatBaseRepository;
    }

    @GetMapping("/attachment")
    public String findAttachmentByTypeAndDataSizeBytesBetween(
            @RequestParam AttachmentType type,
            @RequestParam long dataSizeBytesAfter,
            @RequestParam long dataSizeBytesBefore) {
        var foundAttachment = attachmentRepository.findByTypeAndDataSizeBytesBetween(type, dataSizeBytesAfter, dataSizeBytesBefore);
        if (foundAttachment.isEmpty())
            throw new ResourceNotFoundException();
        return String.join("\n", foundAttachment.stream().map(Attachment::getName).toList());
    }

    @GetMapping("DC")
    public String findDirectChatByUserAAndUserB(
            @RequestParam UUID userA,
            @RequestParam UUID userB) {
        if (userA.equals(userB))
            throw new InvalidParameterException("User can't have direct chat with yourself");
        var firstUser = userRepository.findById(userA);
        if (firstUser.isEmpty())
            throw new ResourceNotFoundException("userA not found");
        var secondUser = userRepository.findById(userB);
        if (secondUser.isEmpty())
            throw new ResourceNotFoundException("userB not found");
        var directChat = directChatRepository.findByUserAAndUserB(firstUser.get(), secondUser.get());
        if (directChat.isEmpty())
            throw new ResourceNotFoundException("direct chat not found");
        return directChat.get().getId().toString();
    }

    @GetMapping("message")
    public String searchMessageInChat(
            @RequestParam UUID chatID,
            @RequestParam String messagePart) {
        var chat = chatBaseRepository.findById(chatID);
        if (chat.isEmpty())
            throw new ResourceNotFoundException("Requested chat is not exist");
        var messages = messageRepository.searchInChat(chatID, messagePart);
        return messages.isEmpty()
                ? "no messages found"
                : String.join("\n\n", messages.stream().map(Message::getContent).toList());
    }
}
