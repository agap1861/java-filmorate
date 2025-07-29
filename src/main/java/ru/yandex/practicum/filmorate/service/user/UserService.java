package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class UserService {

    UserStorage storage;

    public UserService(InMemoryUserStorage storage) {
        this.storage = storage;
    }

    public void addInFriends(long userId, long friendId) {

        validateExistFriends(userId, friendId);
        storage.addInFriends(userId, friendId);


    }

    public void removeFromFriends(long userId, long friendId) {

        validateExistFriends(userId, friendId);
        if (!storage.getFriends().containsKey(userId) || !storage.getFriends().containsKey(friendId)) {
            return;
        }
        if (!storage.getFriends().get(userId).contains(friendId) || !storage.getFriends().get(friendId).contains(userId)) {
            throw new NotFoundException("Not found");
        }
        storage.removeFriend(userId, friendId);


    }

    public List<User> getAllFriendsOfUSerById(long id) {

        if (!storage.exists(id)) {
            throw new NotFoundException("Not found");
        }
        if (!storage.getFriends().containsKey(id)) {
            return List.of();
        }

        return storage.getAllFriendsOfUserById(id);

    }

    public Set<User> getCommonFriends(long userId, long friendId) {
        Set<Long> userSet = storage.getFriends().get(userId);
        Set<Long> friendSet = storage.getFriends().get(friendId);

        validateExistFriends(userId, friendId);

        if (userSet == null || friendSet == null) {
            throw new NotFoundException("Not found");
        }

        return storage.getCommonFriends(userId, friendId);

    }

    public Collection<User> getUsers() {
        return storage.getUsers();
    }

    public User getUserById(long id) {
        User user = storage.getUserById(id).orElseThrow(() -> new NotFoundException("Not found"));
        return user;
    }

    public User postUser(User user) {
        validateOfDataForPost(user);
        return storage.postUser(user);
    }

    public User putUser(User user) {
        validateOfDataForPut(user);
        return storage.putUser(user);
    }

    public void validateOfDataForPost(User user) {

        if (user.getId() == null) {
            Long id = storage.getUsers().stream()
                    .map(User::getId)
                    .max(Long::compareTo)
                    .orElse(0L);
            user.setId(id + 1L);
        }


        if (!StringUtils.hasText(user.getEmail()) || !user.getEmail().contains("@")) {
            log.debug("Invalid email: {}", user.getEmail());
            throw new ValidationException("invalid email");
        }
        if (!StringUtils.hasText(user.getLogin()) || user.getLogin().contains(" ")) {
            log.warn("invalid login: {}", user.getLogin());
            throw new ValidationException("login can not have spaces");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("invalid birthday (in the future): {}", user.getBirthday());
            throw new ValidationException("the date of birth can't be in the future");
        }
        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
            log.debug("name was empty or null, set to login: {}", user.getLogin());
        }
    }


    public void validateOfDataForPut(User user) {
        if (!StringUtils.hasText(user.getEmail()) || !user.getEmail().contains("@")) {
            log.debug("invalid email: {}", user.getEmail());
            throw new ValidationException("invalid email");
        }

        if (!StringUtils.hasText(user.getLogin()) || user.getLogin().contains(" ")) {
            log.warn("invalid login: {}", user.getLogin());
            throw new ValidationException("login can not have spaces");
        }


        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("invalid birthday (in the future): {}", user.getBirthday());
            throw new ValidationException("the date of birth can't be in the future");
        }
    }

    private void validateExistFriends(long userId, long friendId) {
        if (!storage.exists(userId) || !storage.exists(friendId)) {
            throw new NotFoundException("Not found");
        }
    }

}
