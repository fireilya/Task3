package ru.Perminov_Ilya.NauJava.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, length = 50)
    private String tag;

    @Column(name = "login_email", unique = true, nullable = false, length = 254)
    private String loginEmail;

    @Column(name = "password_hash", nullable = false, columnDefinition = "TEXT")
    private String passwordHash;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(nullable = false)
    private Instant registrationDate;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;

}
