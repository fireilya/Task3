package ru.Perminov_Ilya.NauJava.repository;

import org.springframework.data.repository.CrudRepository;
import ru.Perminov_Ilya.NauJava.EmbeddedIDs.GroupChatMemberId;
import ru.Perminov_Ilya.NauJava.entities.GroupChatMember;

import java.util.UUID;

public interface GroupChatMemberRepository extends CrudRepository<GroupChatMember, GroupChatMemberId> {
}
