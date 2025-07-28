package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;


import java.util.Collection;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    UserStorage storage;
    UserService service;

    @Autowired
    public UserController(InMemoryUserStorage storage, UserService service) {
        this.storage = storage;
        this.service = service;
    }


    @GetMapping
    public Collection<User> getUsers() {
        return storage.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) {
        if (storage.getUserById(id).isPresent()) {
            return storage.getUserById(id).get();
        } else {
            throw new NotFoundException("Not found");
        }

    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriendsOfUserById(@PathVariable long id) {

        return service.getAllFriendsOfUSerById(id).stream()
                .map(current -> {
                    if (storage.getUserById(current).isPresent()) {
                        return storage.getUserById(current).get();
                    } else {
                        throw new NotFoundException("Not found");
                    }
                })
                .toList();
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return service.getCommonFriends(id, otherId).stream()
                .map(current -> {
                    if (storage.getUserById(current).isPresent()) {
                        return storage.getUserById(current).get();
                    } else {
                        throw new NotFoundException("Not found");
                    }
                }).toList();
    }

    @PostMapping
    public User postUser(@RequestBody User user) {
        return storage.postUser(user);

    }

    @PutMapping
    public User putUser(@RequestBody User user) {
        return storage.putUser(user);


    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addInFriends(@PathVariable long id, @PathVariable long friendId) {
        if (storage.getUserById(id).isEmpty() || storage.getUserById(friendId).isEmpty()) {
            throw new NotFoundException("Not found");
        }
        service.addInFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFromFriends(@PathVariable long id, @PathVariable long friendId) {
        service.removeFromFriends(id, friendId);
    }

}
