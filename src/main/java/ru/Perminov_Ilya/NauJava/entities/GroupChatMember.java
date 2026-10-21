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
    @MapsId("chatId")
    @JoinColumn(name = "chat_id")
    private GroupChat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private GroupChatMemberRole role;
}
