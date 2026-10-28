package ru.Perminov_Ilya.NauJava.services;

import ru.Perminov_Ilya.NauJava.entities.User;

public interface IUserService {
    User registerUser(final String login, final String password, final String tag, final String name);
    User getUserByLogin(final String login);
}
