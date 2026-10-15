package ru.Perminov_Ilya.NauJava.entities;

import lombok.Getter;
import lombok.Setter;
import ru.Perminov_Ilya.NauJava.entities.enums.ChatType;
import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "group_chats")
@DiscriminatorValue(ChatType.StringValues.GROUP)
public class GroupChat extends ChatBase {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "invite_link", nullable = false, columnDefinition = "TEXT")
    private String inviteLink;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;
}
