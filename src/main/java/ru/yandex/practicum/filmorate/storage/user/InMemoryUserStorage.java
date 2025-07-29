package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import ru.yandex.practicum.filmorate.exception.NotFoundException;

import ru.yandex.practicum.filmorate.model.User;


import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private Map<Long, Set<Long>> friends = new HashMap<>();
    private Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return users.values();

    }

    @Override
    public User postUser(User user) {
        users.put(user.getId(), user);
        log.info("The user was successfully added id = {}", user.getId());
        return user;
    }

    @Override
    public User putUser(User user) {


        User oldVersion = users.get(user.getId());
        if (oldVersion == null) {
            log.warn("user with id {} was not found", user.getId());
            throw new NotFoundException("user not found");
        }
        if (user.getName() != null) {
            oldVersion.setName(user.getName());
        }
        if (user.getBirthday() != null) {
            oldVersion.setBirthday(user.getBirthday());
        }
        if (user.getLogin() != null) {
            oldVersion.setLogin(user.getLogin());
        }
        if (user.getEmail() != null) {
            oldVersion.setEmail(user.getEmail());
        }
        log.info("The user was successfully updated id = {}", oldVersion.getId());
        return oldVersion;
    }


    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public boolean exists(long id) {
        return getUserById(id).isPresent();
    }

    @Override
    public void addInFriends(long userId, long friendId) {
        friends.computeIfAbsent(userId, key -> new HashSet<>()).add(friendId);
        friends.computeIfAbsent(friendId, key -> new HashSet<>()).add(userId);
    }

    @Override
    public Map<Long, Set<Long>> getFriends() {
        return friends;
    }

    @Override
    public List<User> getAllFriendsOfUserById(long id) {
        return friends.get(id).stream()
                .map(userId -> getUserById(userId).orElseThrow(() -> new NotFoundException("Not found")))
                .toList();

    }

    @Override
    public Set<User> getCommonFriends(long userId, long friendId) {
        Set<Long> userSet = friends.get(userId);
        Set<Long> friendSet = friends.get(friendId);
        Set<User> common = new HashSet<>();
        for (long id : userSet) {
            if (friendSet.contains(id)) {
                common.add(getUserById(id).orElseThrow(() -> new NotFoundException("Not found")));
            }
        }
        return common;
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        friends.get(userId).remove(friendId);
        friends.get(friendId).remove(userId);

    }

}
