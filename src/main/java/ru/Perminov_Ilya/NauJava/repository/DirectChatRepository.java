package ru.Perminov_Ilya.NauJava.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.Perminov_Ilya.NauJava.entities.DirectChat;
import ru.Perminov_Ilya.NauJava.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RepositoryRestResource
public interface DirectChatRepository extends CrudRepository<DirectChat, UUID> {
    Optional<DirectChat> findByUserAAndUserB(User userA, User userB);

    @Query("SELECT dc FROM DirectChat dc WHERE dc.userA=:user OR dc.userB=:user")
    List<DirectChat> findAllByUserAOrUserB(@Param("user") User user);
}
