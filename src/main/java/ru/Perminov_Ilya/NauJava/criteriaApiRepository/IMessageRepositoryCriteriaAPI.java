package ru.Perminov_Ilya.NauJava.criteriaApiRepository;

import ru.Perminov_Ilya.NauJava.entities.Message;

import java.util.List;
import java.util.UUID;

public interface IMessageRepositoryCriteriaAPI {
    List<Message> searchInChat(UUID chatId, String search);
}
