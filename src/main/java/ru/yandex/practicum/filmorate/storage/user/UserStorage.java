package ru.yandex.practicum.filmorate.storage.user;


import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

public interface UserStorage {
    Collection<User> getUsers();

    User postUser(User user);

    User putUser(User user);

    Optional<User> getUserById(long id);

    boolean exists(long id);

    void addInFriends(long userId, long friendId);

    Map<Long, Set<Long>> getFriends();

    List<User> getAllFriendsOfUserById(long id);

    Set<User> getCommonFriends(long userId, long friendId);

    void removeFriend(long userId, long friendId);
}
