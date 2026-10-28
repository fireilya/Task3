package ru.Perminov_Ilya.NauJava.services;

import jakarta.transaction.Transactional;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.Perminov_Ilya.NauJava.entities.User;
import ru.Perminov_Ilya.NauJava.entities.enums.UserRole;
import ru.Perminov_Ilya.NauJava.repository.UserRepository;

import java.security.InvalidParameterException;
import java.time.Instant;

@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(rollbackOn = Exception.class)
    public User registerUser(
            final String tag,
            final String login,
            final String password,
            final String name) {
        if (tag == null || tag.isBlank())
            throw new InvalidParameterException("tag must not be empty");

        if (login == null || login.isBlank())
            throw new InvalidParameterException("login must not be empty");

        if (password == null || password.isBlank())
            throw new InvalidParameterException("password must not be empty");

        if (name == null || name.isBlank())
            throw new InvalidParameterException("name must not be empty");

        if (userRepository.existsByLoginEmail(login))
            throw new InvalidParameterException("User with this login already exists");

        if (userRepository.existsByTag(tag))
            throw new InvalidParameterException("User with this tag already exists");

        var user = new User();

        user.setTag(tag);
        user.setLoginEmail(login);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setName(name);
        user.setRegistrationDate(Instant.now());
        user.setRole(UserRole.USER);

        return userRepository.save(user);
    }

    public User getUserByLogin(final String login) {
        if (login == null || login.isBlank())
            throw new InvalidParameterException("login must not be empty");

        return userRepository
                .findByLoginEmail(login)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}