package ru.yandex.practicum.filmorate.service.film;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class FilmService {
    private Map<Long, Set<Long>> filmLikes = new HashMap<>();
    private FilmStorage filmStorage;
    private UserStorage userStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(long idFilm, long idUser) {
        if (userStorage.getUserById(idUser).isEmpty()) {
            throw new NotFoundException("Not found");
        }
        if (filmStorage.findFilmById(idFilm).isEmpty()) {
            throw new NotFoundException("Фильм с id=" + idFilm + " не найден");
        }

        boolean flag = filmLikes.computeIfAbsent(idFilm, key -> new HashSet<>()).add(idUser);
        if (!flag) {
            throw new ValidationException("user already add like in this film");
        }


    }

    public void removeLike(long idFilm, long idUser) {
        Set<Long> users = filmLikes.get(idFilm);
        if (userStorage.getUserById(idUser).isEmpty()) {
            throw new NotFoundException("Not found");
        }
        if (filmStorage.findFilmById(idFilm).isEmpty()) {
            throw new NotFoundException("Фильм с id=" + idFilm + " не найден");
        }
        if (!users.contains(idUser)) {
            throw new NotFoundException("user didn't like this film");
        }
        users.remove(idUser);

    }

    public List<Long> getTopCountFilms(Integer count) {
        return filmLikes.entrySet().stream()
                .sorted((film1, film2)
                        -> Integer.compare(film2.getValue().size(), film1.getValue().size()))
                .limit(count)
                .map(Map.Entry::getKey)
                .toList();

    }

    public int getCountLikeByIdFilm(long id) {
        if (filmLikes.get(id) == null) {
            return 0;
        }
        return filmLikes.get(id).size();
    }


}
