package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.web.bind.annotation.*;


import ru.yandex.practicum.filmorate.model.Film;

import ru.yandex.practicum.filmorate.service.film.FilmService;


import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private FilmService service;


    @Autowired
    FilmController(FilmService service) {

        this.service = service;

    }

    @GetMapping
    public Collection<Film> getFilms() {
        return service.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) {
        Film film = service.getFilmById(id);

        log.info("get film - {}", film.getId());
        return film;

    }

    @GetMapping("/popular")
    public Collection<Film> getTopFilms(@RequestParam Optional<Integer> count, @RequestParam Optional<Integer> genreId, @RequestParam Optional<Integer> year) {
        return service.getTopCountFilms(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getAllFilmsByDirectorSortBy(@PathVariable long directorId, @RequestParam String sortBy) {
        if (sortBy.equals("year")) {
            return service.getAllFilmsByDirectorSortByYear(directorId);
        } else {
            return service.getAllFilmsByDirectorSortByLikes(directorId);
        }
    }

    @PostMapping
    public Film postFilm(@RequestBody Film film) {
        return service.postFilm(film);
    }

    @PutMapping
    public Film putFilm(@RequestBody Film film) {
        return service.putFilm(film);
    }

    @DeleteMapping("/{id}")
    public void removeFilm(@PathVariable long id) {
        service.removeFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        service.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        service.removeLike(id, userId);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam String query,
                                  @RequestParam(defaultValue = "title,director") List<String> by) {
        log.info("Search films with query: '{}', by: {}", query, by);
        return service.searchFilms(query, by);
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFilms(@RequestParam int userId,
                                           @RequestParam int friendId) {
        return service.getCommonFilms(userId, friendId);
    }

}
