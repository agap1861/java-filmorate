package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return users.values();

    }

    @Override
    public User postUser(User user) {
        validateOfDataForPost(user);
        users.put(user.getId(), user);
        log.info("The user was successfully added id = {}", user.getId());
        return user;
    }

    @Override
    public User putUser(User user) {
        validateOfDataForPut(user);

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
    public void validateOfDataForPost(User user) {

        if (user.getId() == null) {
            Long id = users.values().stream()
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

    @Override
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

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

}
