package ru.Perminov_Ilya.NauJava.repository;

import org.springframework.data.repository.CrudRepository;
import ru.Perminov_Ilya.NauJava.entities.ChatBase;

import java.util.UUID;

public interface ChatBaseRepository extends CrudRepository<ChatBase, UUID> {
}
