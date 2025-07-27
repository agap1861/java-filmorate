package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    private Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film postFilm(@RequestBody Film film) {
        validateOfData(film);
        if (film.getId() == null) {
            Long id = films.values().stream()
                    .map(Film::getId)
                    .max(Long::compareTo)
                    .orElse(0L);
            film.setId(id + 1L);
        }
        films.put(film.getId(), film);
        log.info("The movie was successfully added");
        return film;
    }

    @PutMapping
    public Film putFilm(@RequestBody Film film) {
        validateOfData(film);
        Film oldVersion = films.get(film.getId());
        if (oldVersion == null) {
            log.warn("Film with id {} was not found", film.getId());
            throw new NotFoundException("Film not found");
        }
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
        log.info("The movie was successfully updated id = {}",oldVersion.getId());
        return oldVersion;
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

}
