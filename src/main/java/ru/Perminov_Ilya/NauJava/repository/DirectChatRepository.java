package ru.Perminov_Ilya.NauJava.repository;

import org.springframework.data.repository.CrudRepository;
import ru.Perminov_Ilya.NauJava.entities.DirectChat;
import ru.Perminov_Ilya.NauJava.entities.User;

import java.util.Optional;
import java.util.UUID;

public interface DirectChatRepository extends CrudRepository<DirectChat, UUID> {
    Optional<DirectChat> findByUserAAndUserB(User userA, User userB);
}
