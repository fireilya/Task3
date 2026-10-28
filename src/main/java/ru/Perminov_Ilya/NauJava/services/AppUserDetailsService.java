package ru.Perminov_Ilya.NauJava.services;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.Perminov_Ilya.NauJava.entities.enums.UserRole;
import ru.Perminov_Ilya.NauJava.repository.UserRepository;

@Service
public class AppUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) {
        var user = userRepository
                .findByLoginEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var role = user.getRole().getStringValue();

        return User.builder()
                .username(user.getLoginEmail())
                .password(user.getPasswordHash())
                .roles(role)
                .build();
    }
}