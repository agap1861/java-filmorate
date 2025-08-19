package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;


import java.util.Collection;
import java.util.List;
import java.util.Set;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    UserService service;

    @Autowired
    public UserController(UserService service) {

        this.service = service;
    }


    @GetMapping
    public Collection<User> getUsers() {
        return service.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) {
        return service.getUserById(id);

    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriendsOfUserById(@PathVariable long id) {

        return service.getAllFriendsOfUSerById(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return service.getCommonFriends(id, otherId);
    }

    @PostMapping
    public User postUser(@RequestBody User user) {
        return service.postUser(user);

    }

    @PutMapping
    public User putUser(@RequestBody User user) {
        return service.putUser(user);


    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addInFriends(@PathVariable long id, @PathVariable long friendId) {

        service.addInFriends(id, friendId);

    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFromFriends(@PathVariable long id, @PathVariable long friendId) {
        service.removeFromFriends(id, friendId);
    }

}
