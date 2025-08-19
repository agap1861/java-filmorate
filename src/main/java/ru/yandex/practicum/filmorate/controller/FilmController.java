package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;


import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.film.FilmService;


import java.util.Collection;
import java.util.List;


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
    public Collection<Film> getTopFilms(@RequestParam Integer count) {
        return service.getTopCountFilms(count);

    }

    @PostMapping
    public Film postFilm(@RequestBody Film film) {
        return service.postFilm(film);
    }

    @PutMapping
    public Film putFilm(@RequestBody Film film) {
        return service.putFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        service.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        service.removeLike(id, userId);
    }
/*    @GetMapping("/genres")
    public List<Genre> getGenres(){
        return service.getAllGenres();
    }
    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable long id){
        return service.getGenreById(id);
    }
    @GetMapping("/mpa")
    public List<MPA> getAllMpa(){
        return service.getAllMpa();
    }
    @GetMapping("/mpa/{id}")
    public MPA getMpaById(@PathVariable long id){
        return service.getMpaById(id);
    }*/


}
