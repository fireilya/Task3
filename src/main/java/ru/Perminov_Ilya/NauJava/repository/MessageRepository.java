package ru.Perminov_Ilya.NauJava.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.Perminov_Ilya.NauJava.entities.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends CrudRepository<Message, UUID> {
    @Query("SELECT m " +
            "FROM Message m " +
            "LEFT JOIN FETCH m.attachments " +
            "WHERE m.chat.id = :chatId AND m.content LIKE %:search%")
    List<Message> searchInChat(
            @Param("chatId") UUID chatId,
            @Param("search") String search
    );
}
