package ru.yandex.practicum.filmorate.service.film;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class FilmService {

    private FilmStorage filmStorage;
    private UserStorage userStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(long idFilm, long idUser) {
        validateExist(idFilm, idUser);

        boolean flag = filmStorage.addLike(idFilm, idUser);
        if (!flag) {
            throw new ValidationException("user already add like in this film");
        }


    }

    public void removeLike(long idFilm, long idUser) {

        validateExist(idFilm, idUser);

        Set<Long> users = filmStorage.getFilmsLikes(idFilm);
        if (!users.contains(idUser)) {
            throw new NotFoundException("user didn't like this film");
        }
        filmStorage.removeLike(idFilm, idUser);

    }

    public List<Film> getTopCountFilms(Integer count) {
        if (count == null) {
            final int basic = 10;
            return filmStorage.getTopFilms(basic);
        } else {
            return filmStorage.getTopFilms(count);
        }

    }

    public void validateOfData(Film film) {
        if (!StringUtils.hasText(film.getName())) {
            log.warn("Film name is empty");
            throw new ValidationException("Name can not be empty");
        }
        if (film.getDescription().length() > 200) {
            log.warn("More then 200 symbols, {}", film.getDescription().length());
            throw new ValidationException("Limited of description is 200 symbols");
        }
        final LocalDate earliestReleaseDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(earliestReleaseDate)) {
            log.warn("Invalid release date {}", film.getReleaseDate());
            throw new ValidationException("Date of realize can not be before 1895.12.28");
        }
        if (film.getDuration().isNegative() || film.getDuration().isZero()) {
            log.warn("Duration not positive {}", film.getDuration());
            throw new ValidationException("Duration must be positive");
        }

    }

    public Film postFilm(Film film) {
        validateOfData(film);
        return filmStorage.postFilm(film);
    }

    public Film putFilm(Film film) {
        validateOfData(film);
        if (filmStorage.findFilmById(film.getId()).isEmpty()) {
            log.warn("Film with id {} was not found", film.getId());
            throw new NotFoundException("Film not found");
        }
        return filmStorage.putFilm(film);
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(long id) {
        Film film = filmStorage.findFilmById(id).orElseThrow(() -> new NotFoundException("not found"));
        return film;
    }

    private void validateExist(long idFilm, long idUser) {
        userStorage.getUserById(idUser)
                .orElseThrow(() -> new NotFoundException("Not found"));
        filmStorage.findFilmById(idFilm)
                .orElseThrow(() -> new NotFoundException("Not found"));
    }


}
