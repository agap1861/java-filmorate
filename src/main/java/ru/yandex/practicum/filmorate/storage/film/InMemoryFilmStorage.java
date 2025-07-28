package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private Map<Long, Film> films = new HashMap<>();


    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film postFilm(Film film) {
        validateOfData(film);
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
        log.info("The movie was successfully updated id = {}", oldVersion.getId());
        return oldVersion;
    }

    @Override
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

    @Override
    public Optional<Film> findFilmById(long id) {
        return Optional.ofNullable(films.get(id));
    }
}
