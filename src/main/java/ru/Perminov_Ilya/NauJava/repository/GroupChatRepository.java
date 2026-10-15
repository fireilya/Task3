package ru.Perminov_Ilya.NauJava.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.Perminov_Ilya.NauJava.entities.GroupChat;
import ru.Perminov_Ilya.NauJava.entities.GroupChatMember;
import ru.Perminov_Ilya.NauJava.entities.User;
import ru.Perminov_Ilya.NauJava.entities.enums.GroupChatMemberRole;

import java.util.List;
import java.util.UUID;

public interface GroupChatRepository extends CrudRepository<GroupChat, UUID> {
}
