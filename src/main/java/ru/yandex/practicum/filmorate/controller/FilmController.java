package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;


import java.util.Collection;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private FilmStorage storage;
    private FilmService service;


    @Autowired
    FilmController(InMemoryFilmStorage storage, FilmService service) {
        this.storage = storage;
        this.service = service;

    }

    @GetMapping
    public Collection<Film> getFilms() {
        return storage.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) {

        Optional<Film> film = storage.findFilmById(id);

        if (film.isEmpty()) {
            throw new NotFoundException("Not find");
        }
        log.info("get film - {}", film.get().getId());
        return film.get();

    }

    @GetMapping("/popular")
    public Collection<Film> getTopFilms(@RequestParam Integer count) {
        if (count == null) {
            return service.getTopCountFilms(count)
                    .stream()
                    .map(id -> {
                        if (storage.findFilmById(id).isPresent()) {
                            return storage.findFilmById(id).get();
                        } else {
                            throw new NotFoundException("Not found");
                        }

                    })
                    .toList();

        } else {
            final Integer top10 = 10;
            return service.getTopCountFilms(top10)
                    .stream()
                    .map(id -> {

                        if (storage.findFilmById(id).isPresent()) {
                            return storage.findFilmById(id).get();
                        } else {
                            throw new NotFoundException("Not found");
                        }
                    })
                    .toList();
        }

    }

    @PostMapping
    public Film postFilm(@RequestBody Film film) {
        return storage.postFilm(film);
    }

    @PutMapping
    public Film putFilm(@RequestBody Film film) {
        return storage.putFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        service.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        service.removeLike(id, userId);
    }


}
