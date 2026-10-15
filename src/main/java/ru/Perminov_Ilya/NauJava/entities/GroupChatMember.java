package ru.Perminov_Ilya.NauJava.entities;

import lombok.Getter;
import lombok.Setter;
import ru.Perminov_Ilya.NauJava.EmbeddedIDs.GroupChatMemberId;
import ru.Perminov_Ilya.NauJava.entities.enums.GroupChatMemberRole;
import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "group_chat_members")
public class GroupChatMember {
    @EmbeddedId
    private GroupChatMemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("chatId") // Связывает это поле с id.chatId из GroupChatMemberId
    @JoinColumn(name = "chat_id") // nullable=false здесь уже подразумевается первичным ключом
    private GroupChat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId") // Связывает это поле с id.userId из GroupChatMemberId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private GroupChatMemberRole role;
}
