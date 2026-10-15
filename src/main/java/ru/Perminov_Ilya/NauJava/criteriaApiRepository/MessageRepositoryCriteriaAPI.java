package ru.Perminov_Ilya.NauJava.criteriaApiRepository;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import ru.Perminov_Ilya.NauJava.entities.Message;

import java.util.List;
import java.util.UUID;

@Repository
public class MessageRepositoryCriteriaAPI implements IMessageRepositoryCriteriaAPI{

    private final EntityManager em;
    public MessageRepositoryCriteriaAPI(EntityManager em) {
        this.em = em;
    }
    @Override
    public List<Message> searchInChat(UUID chatId, String search) {

        var cb = em.getCriteriaBuilder();
        var query = cb.createQuery(Message.class);
        var message = query.from(Message.class);

        query.where(
                cb.equal(message.get("chat").get("id"), chatId),
                cb.like(message.get("content"), "%" + search + "%")
        );

        return em.createQuery(query).getResultList();
    }
}
