package ru.Perminov_Ilya.NauJava.repository;

import org.springframework.data.repository.CrudRepository;
import ru.Perminov_Ilya.NauJava.entities.User;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {
}
