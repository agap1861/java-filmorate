package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.*;

public interface FilmStorage {
    Collection<Film> getFilms();

    Film postFilm(Film film);

    Film putFilm(Film film);

    Optional<Film> findFilmById(long id);

    boolean addLike(long id, long idUser);

    Set<Long> getFilmsLikes(long id);

    List<Film> getTopFilms(Integer count);

    void removeLike(long idFilm, long idUser);

    List<Genre> getAllGenres();

    Optional<Genre> getGenreById(long id);

    List<MPA> getAllMpa();

    Optional<MPA> getMpaById(long id);


}
