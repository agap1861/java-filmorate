package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import ru.yandex.practicum.filmorate.exception.DuplicateFriendException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class UserService {

    UserStorage storage;

    public UserService(@Qualifier("userDbStorage") UserStorage storage) {
        this.storage = storage;
    }

    public void addInFriends(long userId, long friendId) {

        validateExistFriends(userId, friendId);

        if (storage.haveUserFriend(userId, friendId)) {
            throw new DuplicateFriendException("These users already in friends list");
        } else {
            storage.addInFriends(userId, friendId);
        }


    }

    public void removeFromFriends(long userId, long friendId) {

        validateExistFriends(userId, friendId);


        if (!storage.haveUserFriend(userId, friendId)) {
            return;

        }
        storage.removeFriend(userId, friendId);


    }

    public List<User> getAllFriendsOfUSerById(long id) {

        isExistUser(id);
        if (!storage.isExistListOfFriends(id)) {
            return List.of();
        }


        return storage.getAllFriendsOfUserById(id);

    }

    public Set<User> getCommonFriends(long userId, long friendId) {
        List<User> userSet = storage.getAllFriendsOfUserById(userId);
        List<User> friendSet = storage.getAllFriendsOfUserById(friendId);

        validateExistFriends(userId, friendId);

        if (userSet == null || friendSet == null) {
            return Set.of();
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
        isExistUser(user.getId());
        validateOfDataForPut(user);
        return storage.putUser(user);
    }

    public void removeUser(long userId) {
        isExistUser(userId);
        storage.removeUser(userId);
    }

    public void validateOfDataForPost(User user) {

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

    private void isExistUser(long id) {
        if (!storage.exists(id)) {
            throw new NotFoundException("Not found");
        }
    }

}
