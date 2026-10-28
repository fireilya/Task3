package ru.Perminov_Ilya.NauJava.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.Perminov_Ilya.NauJava.entities.User;

import java.util.Optional;
import java.util.UUID;

@RepositoryRestResource
public interface UserRepository extends CrudRepository<User, UUID> {
    Optional<User> findByLoginEmail(String loginEmail);

    boolean existsByLoginEmail(String loginEmail);

    boolean existsByTag(String tag);
}