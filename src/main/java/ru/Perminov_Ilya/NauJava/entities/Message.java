package ru.Perminov_Ilya.NauJava.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "messages",
        check = {@CheckConstraint(constraint = "reply_to_message_id <> id")}
)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private ChatBase chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_message_id")
    private Message replyToMessage;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    @Column(name = "edited_at")
    private Instant editedAt;

    @OneToMany(mappedBy = "message", fetch = FetchType.LAZY,  cascade = CascadeType.ALL)
    private List<Attachment> attachments;

    @AssertTrue(message = "Message can't reply on yourself")
    public boolean isReplyOnYourself() {
        if (replyToMessage == null || id == null || replyToMessage.id == null) {
            return true;
        }
        return !id.equals(replyToMessage.getId());
    }
}
