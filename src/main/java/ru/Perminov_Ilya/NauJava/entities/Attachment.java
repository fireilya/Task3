package ru.Perminov_Ilya.NauJava.entities;

import lombok.Getter;
import lombok.Setter;
import ru.Perminov_Ilya.NauJava.entities.enums.AttachmentType;
import jakarta.persistence.*;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "attachments")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AttachmentType type;

    @Column(
            name = "data_size_bytes",
            nullable = false,
            check = {@CheckConstraint(constraint = "data_size_bytes >= 0")})
    private long dataSizeBytes;

    @Column(name = "data_url", nullable = false, columnDefinition = "TEXT")
    private String dataUrl;
}
