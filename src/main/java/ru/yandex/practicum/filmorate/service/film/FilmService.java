package ru.yandex.practicum.filmorate.service.film;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import ru.yandex.practicum.filmorate.storage.user.UserStorage;


import java.time.LocalDate;
import java.util.*;


@Slf4j
@Service
public class FilmService {

    private FilmStorage filmStorage;
    private UserStorage userStorage;
    private DirectorStorage directorStorage;


    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage, DirectorStorage directorStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.directorStorage = directorStorage;
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

        final int basic = count != null ? count : 10;
        return filmStorage.getTopFilms(basic);

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
        if (film.getMpa() != null) {
            MPA mpa = filmStorage.getMpaById(film.getMpa().getId()).orElseThrow(() -> new NotFoundException("not found"));
            film.setMpa(mpa);
        }
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {

            List<Long> idsGenres = film.getGenres().stream()
                    .map(Genre::getId)
                    .distinct()
                    .toList();

            List<Genre> genres = filmStorage.getGenresByIds(idsGenres);
            if (genres.size() != idsGenres.size()) {
                throw new NotFoundException("nod found");
            }
            film.setGenres(genres);

        } else {
            film.setGenres(List.of());
        }
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            List<Long> idsDirectors = film.getDirectors().stream()
                    .map(Director::getId)
                    .distinct()
                    .toList();
            List<Director> directors = directorStorage.getDirectorsByIds(idsDirectors);
            if (directors.size() != idsDirectors.size()) {
                throw new NotFoundException("not found");
            }
            film.setDirectors(directors);
        } else {
            film.setDirectors(List.of());
        }

    }

    public Film postFilm(Film film) {
        validateOfData(film);
        return filmStorage.postFilm(film);
    }

    public Film putFilm(Film film) {
        if (!filmStorage.isExistFilmById(film.getId())) {
            throw new NotFoundException("not found");
        }
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

    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Genre getGenreById(long id) {
        Genre genre = filmStorage.getGenreById(id).orElseThrow(() -> new NotFoundException("not found"));
        return genre;

    }

    public List<MPA> getAllMpa() {
        return filmStorage.getAllMpa();
    }

    public MPA getMpaById(long id) {
        MPA mpa = filmStorage.getMpaById(id).orElseThrow(() -> new NotFoundException("not found"));
        return mpa;
    }

    public List<Film> getAllFilmsByDirectorSortByYear(long id ) {
        if (directorStorage.isExistDirectorById(id)) {
            return filmStorage.getAllFilmsByDirectorSortByYear(id);
        } else {
            return null;
        }
    }

    public List<Film> getAllFilmsByDirectorSortByLikes(long id) {
        try {
            if (directorStorage.isExistDirectorById(id)) {
                return filmStorage.getAllFilmsByDirectorSortByLikes(id);
            } else {
                return null;
            }
        }catch (Exception e){
            log.info(e.getMessage());
        }
     return null;
    }


}
