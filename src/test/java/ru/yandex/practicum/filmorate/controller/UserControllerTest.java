package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.stream.Stream;


class UserControllerTest {
    private UserController controller;

    @BeforeEach
    public void createController() {
        controller = new UserController();
    }


    @ParameterizedTest
    @MethodSource("invalidArgsFactory")
    public void shouldNotValidate(User user) {
        Assertions.assertThrows(ValidationException.class, () -> controller.validateOfDataForPost(user));

    }

    @ParameterizedTest
    @MethodSource("validateArgsFactory")
    public void shouldValidate(User user) {
        Assertions.assertDoesNotThrow(() -> controller.validateOfDataForPost(user));

    }

    public static Stream<User> invalidArgsFactory() {
        User user = new User(1L, "@w", "login", "name", LocalDate.of(2000, 1, 1));
        User user1 = new User(1L, "w", "login", "name", LocalDate.of(2000, 1, 1));
        User user2 = new User(1L, "", "login", "name", LocalDate.of(2000, 1, 1));
        User user3 = new User(1L, "@w", "", "name", LocalDate.of(2000, 1, 1));
        User user4 = new User(1L, "@w", "1 2", "name", LocalDate.of(2000, 1, 1));
        User user5 = new User(1L, "@w", "1 2", "name", LocalDate.of(2030, 1, 1));
        return Stream.of(user1, user2, user3, user4, user5);

    }

    public static Stream<User> validateArgsFactory() {
        User user = new User(1L, "@w", "login", "name", LocalDate.of(2000, 1, 1));
        User user1 = new User(1L, "@w", "login", "name", LocalDate.of(2025, 7, 13));

        return Stream.of(user, user1);

    }


}