package ru.yandex.practicum.filmorate.storage.film.inMemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.NotFoundException;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;


import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private Map<Long, Film> films = new HashMap<>();
    private Map<Long, Set<Long>> filmsLikes = new HashMap<>();


    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film postFilm(Film film) {
        if (film.getId() == null) {
            Long id = films.values().stream()
                    .map(Film::getId)
                    .max(Long::compareTo)
                    .orElse(0L);
            film.setId(id + 1L);
        }
        films.put(film.getId(), film);
        log.info("The movie was successfully added. id = {}", film.getId());
        return film;
    }

    @Override
    public Film putFilm(Film film) {
        Film oldVersion = films.get(film.getId());

        if (film.getName() != null) {
            oldVersion.setName(film.getName());
        }
        if (film.getDescription() != null) {
            oldVersion.setDescription(film.getDescription());
        }
        if (film.getDuration() != null) {
            oldVersion.setDuration(film.getDuration());
        }
        if (film.getReleaseDate() != null) {
            oldVersion.setReleaseDate(film.getReleaseDate());
        }
        log.info("The movie was successfully updated id = {}", oldVersion.getId());
        return oldVersion;
    }

    @Override
    public void removeFilm(long filmId) {
    }

    @Override
    public Optional<Film> findFilmById(long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        return filmsLikes.entrySet().stream()
                .sorted((film1, film2)
                        -> Integer.compare(film2.getValue().size(), film1.getValue().size()))
                .limit(count)
                .map(Map.Entry::getKey)
                .map(id -> findFilmById(id).orElseThrow(() -> new NotFoundException("Not found")))
                .toList();


    }

    @Override
    public boolean addLike(long id, long idUser) {

        return filmsLikes.computeIfAbsent(id, key -> new HashSet<>()).add(idUser);
    }

    @Override
    public Set<Long> getFilmsLikes(long id) {
        return filmsLikes.get(id);
    }

    @Override
    public void removeLike(long idFilm, long idUser) {
        filmsLikes.get(idFilm).remove(idFilm);
    }

    @Override
    public List<Genre> getAllGenres() {
        return List.of();
    }

    @Override
    public Optional<Genre> getGenreById(long id) {
        return Optional.empty();
    }

    @Override
    public List<MPA> getAllMpa() {
        return List.of();
    }

    @Override
    public Optional<MPA> getMpaById(long id) {
        return Optional.empty();
    }

    @Override
    public List<Genre> getGenresByIds(List<Long> ids) {
        return List.of();
    }

    @Override
    public boolean isExistFilmById(long id) {
        return false;
    }

    @Override
    public List<Film> getAllFilmsByDirectorSortByYear(long id) {
        return List.of();
    }

    @Override
    public List<Film> getAllFilmsByDirectorSortByLikes(long id) {
        return List.of();
    }
}
