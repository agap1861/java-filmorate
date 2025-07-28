package ru.yandex.practicum.filmorate.storage.user;


import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> getUsers();

    User postUser(User user);

    User putUser(User user);

    void validateOfDataForPost(User user);

    void validateOfDataForPut(User user);

    Optional<User> getUserById(long id);
}
