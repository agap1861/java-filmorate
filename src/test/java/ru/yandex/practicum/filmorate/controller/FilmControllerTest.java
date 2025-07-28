package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;


class FilmControllerTest {
    FilmStorage storage;

    @BeforeEach
    public void createController() {
        storage = new InMemoryFilmStorage();
    }


    @ParameterizedTest
    @MethodSource("wrongArgsFactory")
    public void shouldNotValidate(Film film) {
        Assertions.assertThrows(ValidationException.class, () -> storage.validateOfData(film));
    }

    @ParameterizedTest
    @MethodSource("validArgFactory")
    public void shouldValidate(Film film) {
        Assertions.assertDoesNotThrow(() -> storage.validateOfData(film));

    }

    static Stream<Film> wrongArgsFactory() {
        Film film = new Film(1L, null, "descr", LocalDate.of(2000, 1, 1),
                Duration.of(30, ChronoUnit.MINUTES));
        Film film1 = new Film(1L, "", "descr", LocalDate.of(2000, 1, 1),
                Duration.of(30, ChronoUnit.MINUTES));
        String desr = "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque " +
                "laudantium, totam rem aperiam eaque ipsa, quae ab illo inventore veritatis et quasi architecto beatae" +
                " vitae dicta .";
        Film film2 = new Film(1L, "name", desr, LocalDate.of(2000, 1, 1),
                Duration.of(30, ChronoUnit.MINUTES));
        LocalDate date = LocalDate.of(1895, 12, 27);
        Film film3 = new Film(1L, "name", "descr", date,
                Duration.of(30, ChronoUnit.MINUTES));
        Film film4 = new Film(1L, "name", "descr", LocalDate.of(2000, 1, 1),
                Duration.of(-30, ChronoUnit.MINUTES));
        Film film5 = new Film(1L, "name", "descr", LocalDate.of(2000, 1, 1),
                Duration.of(0, ChronoUnit.MINUTES));
        return Stream.of(film, film1, film2, film3, film4, film5);
    }

    static Stream<Film> validArgFactory() {
        String desr = "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque " +
                "laudantium, totam rem aperiam eaque ipsa, quae ab illo inventore veritatis et quasi architecto beatae" +
                " vitae dict .";
        Film film = new Film(1L, "name", desr, LocalDate.of(2000, 1, 1),
                Duration.of(30, ChronoUnit.MINUTES));
        String desr1 = "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque " +
                "laudantium, totam rem aperiam eaque ipsa, quae ab illo inventore veritatis et quasi architecto beatae" +
                " vitae dict .";
        Film film1 = new Film(1L, "name", desr1, LocalDate.of(2000, 1, 1),
                Duration.of(30, ChronoUnit.MINUTES));
        LocalDate date = LocalDate.of(1895, 12, 28);
        LocalDate date1 = LocalDate.of(1895, 12, 29);
        Film film2 = new Film(1L, "name", "descr", date,
                Duration.of(30, ChronoUnit.MINUTES));
        Film film3 = new Film(1L, "name", "descr", date1,
                Duration.of(30, ChronoUnit.MINUTES));
        return Stream.of(film, film1, film2, film3);
    }

}