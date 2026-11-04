package ru.Perminov_Ilya.NauJava.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.Perminov_Ilya.NauJava.entities.enums.ReportStatus;

import java.util.UUID;

@Entity
@Table(name = "reports")
@Getter
@Setter
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @Column(columnDefinition = "TEXT")
    private String content;
}
