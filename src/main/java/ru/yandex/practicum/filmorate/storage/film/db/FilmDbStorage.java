package ru.yandex.practicum.filmorate.storage.film.db;


import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.MPARowMapper;

import java.sql.Date;


import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Repository

public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private final GenreRowMapper genreMapper;
    private final MPARowMapper mpaMapper;

    private static final String GET_ALL_FILMS = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
            "f.mpa_id, m.name AS mpa_name, " +
            "GROUP_CONCAT(g.id) AS genres_id, " +
            "GROUP_CONCAT(g.name) AS genre_names, " +
            "GROUP_CONCAT(d.id) AS directors_id, " +
            "GROUP_CONCAT(d.name) AS director_names " +
            "FROM films AS f " +
            "INNER JOIN  mpa AS m ON m.id=f.mpa_id " +
            "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
            "LEFT JOIN genres AS g ON g.id = fg.genre_id " +
            "LEFT JOIN films_directors AS fd ON fd.film_id = f.id " +
            "LEFT JOIN directors AS d ON d.id = fd.director_id " +
            "GROUP BY f.id";
    private static final String INSERT_FILM = "INSERT INTO films (name,description,release_date,duration,mpa_id)" +
            " VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_FILM = "UPDATE films SET ";
    private static final String GET_FILM_BY_ID = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
            "f.mpa_id, m.name AS mpa_name, " +
            "GROUP_CONCAT(g.id) AS genres_id, " +
            "GROUP_CONCAT(g.name) AS genre_names, " +
            "GROUP_CONCAT(d.id) AS directors_id, " +
            "GROUP_CONCAT(d.name) AS director_names " +
            "FROM films AS f " +
            "INNER JOIN  mpa AS m ON m.id=f.mpa_id " +
            "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
            "LEFT JOIN genres AS g ON g.id = fg.genre_id " +
            "LEFT JOIN films_directors AS fd ON fd.film_id = f.id " +
            "LEFT JOIN directors AS d ON d.id = fd.director_id " +
            "WHERE f.id = ? " +
            "GROUP BY f.id";
    private static final String REMOVE_LIKE = "DELETE FROM films_like WHERE film_id = ? AND user_id = ?";
    private static final String REMOVE_FILM = "DELETE FROM films WHERE id = ?";
    private static final String GET_TOP_FILMS_LIMITED = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
            "f.mpa_id, m.name AS mpa_name, " +
            "GROUP_CONCAT(g.id) AS genres_id, " +
            "GROUP_CONCAT(g.name) AS genre_names, " +
            "GROUP_CONCAT(d.id) AS directors_id, " +
            "GROUP_CONCAT(d.name) AS director_names " +
            "FROM films AS f " +
            "LEFT OUTER JOIN films_like as fl ON fl.film_id = f.id " +
            "INNER JOIN  mpa AS m ON m.id=f.mpa_id " +
            "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
            "LEFT JOIN genres AS g ON g.id = fg.genre_id " +
            "LEFT JOIN films_directors AS fd ON fd.film_id = f.id " +
            "LEFT JOIN directors AS d ON d.id = fd.director_id " +
            "GROUP BY f.id " +
            "ORDER BY COUNT(fl.user_id) DESC " +
            "LIMIT ? ";
    private static final String GET_TOP_FILMS_BY_GENRE_AND_YEAR_LIMITED = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
            "f.mpa_id, m.name AS mpa_name, " +
            "GROUP_CONCAT(g.id) AS genres_id, " +
            "GROUP_CONCAT(g.name) AS genre_names, " +
            "GROUP_CONCAT(d.id) AS directors_id, " +
            "GROUP_CONCAT(d.name) AS director_names " +
            "FROM films AS f " +
            "LEFT OUTER JOIN films_like as fl ON fl.film_id = f.id " +
            "INNER JOIN  mpa AS m ON m.id=f.mpa_id " +
            "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
            "LEFT JOIN genres AS g ON g.id = fg.genre_id " +
            "LEFT JOIN films_directors AS fd ON fd.film_id = f.id " +
            "LEFT JOIN directors AS d ON d.id = fd.director_id " +
            "WHERE f.id IN (SELECT film_genres.film_id FROM film_genres WHERE film_genres.genre_id = ?) " +
            "AND EXTRACT(YEAR FROM f.release_date) = ? " +
            "GROUP BY f.id " +
            "ORDER BY COUNT(fl.user_id) DESC " +
            "LIMIT ? ";
    private static final String GET_TOP_FILMS_BY_YEAR_LIMITED = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
            "f.mpa_id, m.name AS mpa_name, " +
            "GROUP_CONCAT(g.id) AS genres_id, " +
            "GROUP_CONCAT(g.name) AS genre_names, " +
            "GROUP_CONCAT(d.id) AS directors_id, " +
            "GROUP_CONCAT(d.name) AS director_names " +
            "FROM films AS f " +
            "LEFT OUTER JOIN films_like as fl ON fl.film_id = f.id " +
            "INNER JOIN  mpa AS m ON m.id=f.mpa_id " +
            "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
            "LEFT JOIN genres AS g ON g.id = fg.genre_id " +
            "LEFT JOIN films_directors AS fd ON fd.film_id = f.id " +
            "LEFT JOIN directors AS d ON d.id = fd.director_id " +
            "WHERE EXTRACT(YEAR FROM f.release_date) = ? " +
            "GROUP BY f.id " +
            "ORDER BY COUNT(fl.user_id) DESC " +
            "LIMIT ? ";
    private static final String GET_TOP_FILMS_BY_GENRE_LIMITED = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
            "f.mpa_id, m.name AS mpa_name, " +
            "GROUP_CONCAT(g.id) AS genres_id, " +
            "GROUP_CONCAT(g.name) AS genre_names, " +
            "GROUP_CONCAT(d.id) AS directors_id, " +
            "GROUP_CONCAT(d.name) AS director_names " +
            "FROM films AS f " +
            "LEFT OUTER JOIN films_like as fl ON fl.film_id = f.id " +
            "INNER JOIN  mpa AS m ON m.id=f.mpa_id " +
            "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
            "LEFT JOIN genres AS g ON g.id = fg.genre_id " +
            "LEFT JOIN films_directors AS fd ON fd.film_id = f.id " +
            "LEFT JOIN directors AS d ON d.id = fd.director_id " +
            "WHERE f.id IN (SELECT film_genres.film_id FROM film_genres WHERE film_genres.genre_id = ?)" +
            "GROUP BY f.id " +
            "ORDER BY COUNT(fl.user_id) DESC " +
            "LIMIT ? ";
    private static final String GET_FILMS_BY_DIRECTOR_SORT_BY_YEAR = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
            "f.mpa_id, m.name AS mpa_name, " +
            "GROUP_CONCAT(g.id) AS genres_id, " +
            "GROUP_CONCAT(g.name) AS genre_names, " +
            "GROUP_CONCAT(d.id) AS directors_id, " +
            "GROUP_CONCAT(d.name) AS director_names " +
            "FROM films AS f " +
            "INNER JOIN  mpa AS m ON m.id=f.mpa_id " +
            "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
            "LEFT JOIN genres AS g ON g.id = fg.genre_id " +
            "LEFT JOIN films_directors AS fd ON fd.film_id = f.id " +
            "LEFT JOIN directors AS d ON d.id = fd.director_id " +
            "WHERE d.id = ? " +
            "GROUP BY f.id " +
            "ORDER BY f.release_date ASC ";
    private static final String GET_FILMS_BY_DIRECTOR_SORT_BY_LIKE = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
            "f.mpa_id, m.name AS mpa_name, " +
            "GROUP_CONCAT(g.id) AS genres_id, " +
            "GROUP_CONCAT(g.name) AS genre_names, " +
            "GROUP_CONCAT(d.id) AS directors_id, " +
            "GROUP_CONCAT(d.name) AS director_names " +
            "FROM films AS f " +
            "LEFT OUTER JOIN films_like as fl ON fl.film_id = f.id " +
            "INNER JOIN  mpa AS m ON m.id=f.mpa_id " +
            "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
            "LEFT JOIN genres AS g ON g.id = fg.genre_id " +
            "LEFT JOIN films_directors AS fd ON fd.film_id = f.id " +
            "LEFT JOIN directors AS d ON d.id = fd.director_id " +
            "WHERE d.id = ? " +
            "GROUP BY f.id " +
            "ORDER BY COUNT(fl.user_id) DESC ";
    private static final String ADD_LIKE = "INSERT INTO films_like (film_id,user_id) VALUES (?, ?)";
    private static final String GET_LIKES_FOR_FILM = "SELECT user_id FROM films_like WHERE film_id = ?";
    private static final String GET_ALL_GENRES = "SELECT * FROM genres ORDER BY id";
    private static final String GET_GENRE_BY_ID = "SELECT * FROM genres WHERE id = ?";
    private static final String GET_ALL_MPA = "SELECT * FROM mpa ORDER BY id";
    private static final String GET_MPA_BY_ID = "SELECT * FROM mpa WHERE id = ?";
    private static final String ADD_IN_FILMS_GENRES = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?) ";
    private static final String EXIST = "SELECT EXISTS(SELECT 1 FROM films WHERE id = ?) ";
    private static final String ADD_IN_FILMS_DIRECTOR = "INSERT INTO films_directors (film_id, director_id) VALUES (?, ?) ";
    private static final String DELETE_DIRECTORS = "DELETE FROM films_directors WHERE film_id = ? ";
    private static final String SEARCH_FILMS_BY_TITLE =
            "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                    "f.mpa_id, m.name AS mpa_name, " +
                    "GROUP_CONCAT(DISTINCT g.id) AS genres_id, " +
                    "GROUP_CONCAT(DISTINCT g.name) AS genre_names, " +
                    "GROUP_CONCAT(DISTINCT d.id) AS directors_id, " +
                    "GROUP_CONCAT(DISTINCT d.name) AS director_names " +
                    "FROM films AS f " +
                    "INNER JOIN mpa AS m ON m.id = f.mpa_id " +
                    "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
                    "LEFT JOIN genres AS g ON g.id = fg.genre_id " +
                    "LEFT JOIN films_directors AS fd ON fd.film_id = f.id " +
                    "LEFT JOIN directors AS d ON d.id = fd.director_id " +
                    "LEFT JOIN films_like AS fl ON fl.film_id = f.id " +
                    "WHERE LOWER(f.name) LIKE '%' || ? || '%' " +
                    "GROUP BY f.id " +
                    "ORDER BY COUNT(DISTINCT fl.user_id) DESC";
    private static final String SEARCH_FILMS_BY_DIRECTOR =
            "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                    "f.mpa_id, m.name AS mpa_name, " +
                    "GROUP_CONCAT(DISTINCT g.id) AS genres_id, " +
                    "GROUP_CONCAT(DISTINCT g.name) AS genre_names, " +
                    "GROUP_CONCAT(DISTINCT d.id) AS directors_id, " +
                    "GROUP_CONCAT(DISTINCT d.name) AS director_names " +
                    "FROM films AS f " +
                    "INNER JOIN mpa AS m ON m.id = f.mpa_id " +
                    "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
                    "LEFT JOIN genres AS g ON g.id = fg.genre_id " +
                    "LEFT JOIN films_directors AS fd ON fd.film_id = f.id " +
                    "LEFT JOIN directors AS d ON d.id = fd.director_id " +
                    "LEFT JOIN films_like AS fl ON fl.film_id = f.id " +
                    "WHERE LOWER(d.name) LIKE '%' || ? || '%' " +
                    "GROUP BY f.id " +
                    "ORDER BY COUNT(DISTINCT fl.user_id) DESC";
    private static final String SEARCH_FILMS_BY_TITLE_AND_DIRECTOR =
            "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                    "f.mpa_id, m.name AS mpa_name, " +
                    "GROUP_CONCAT(DISTINCT g.id) AS genres_id, " +
                    "GROUP_CONCAT(DISTINCT g.name) AS genre_names, " +
                    "GROUP_CONCAT(DISTINCT d.id) AS directors_id, " +
                    "GROUP_CONCAT(DISTINCT d.name) AS director_names " +
                    "FROM films AS f " +
                    "INNER JOIN mpa AS m ON m.id = f.mpa_id " +
                    "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
                    "LEFT JOIN genres AS g ON g.id = fg.genre_id " +
                    "LEFT JOIN films_directors AS fd ON fd.film_id = f.id " +
                    "LEFT JOIN directors AS d ON d.id = fd.director_id " +
                    "LEFT JOIN films_like AS fl ON fl.film_id = f.id " +
                    "WHERE LOWER(f.name) LIKE '%' || ? || '%' OR LOWER(d.name) LIKE '%' || ? || '%' " +
                    "GROUP BY f.id " +
                    "ORDER BY COUNT(DISTINCT fl.user_id) DESC";
    private static final String GET_COMMON_FILMS =
            "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                    "f.mpa_id, m.name AS mpa_name, " +
                    "GROUP_CONCAT(DISTINCT g.id) AS genres_id, " +
                    "GROUP_CONCAT(DISTINCT g.name) AS genre_names, " +
                    "GROUP_CONCAT(DISTINCT d.id) AS directors_id, " +
                    "GROUP_CONCAT(DISTINCT d.name) AS director_names, " +
                    "COUNT(DISTINCT fl.user_id) AS likes_count " +
                    "FROM films AS f " +
                    "INNER JOIN mpa AS m ON m.id = f.mpa_id " +
                    "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
                    "LEFT JOIN genres AS g ON g.id = fg.genre_id " +
                    "LEFT JOIN films_directors AS fd ON fd.film_id = f.id " +
                    "LEFT JOIN directors AS d ON d.id = fd.director_id " +
                    "LEFT JOIN films_like AS fl ON fl.film_id = f.id " +
                    "WHERE f.id IN ( " +
                    "    SELECT film_id FROM films_like WHERE user_id = ? " +
                    ") AND f.id IN ( " +
                    "    SELECT film_id FROM films_like WHERE user_id = ? " +
                    ") " +
                    "GROUP BY f.id, m.name " +
                    "ORDER BY likes_count DESC";

    private static final String DELETE_GENRE_FROM_MOVIES = "DELETE FROM film_genres WHERE film_id = ? ";
    private static final String GET_GENRES_BY_FILM = "SELECT g.*  FROM GENRES AS g " +
            "LEFT JOIN film_genres AS fg ON fg.genre_id = g.id " +
            "WHERE fg.film_id = ? ";
    private static final String DELETE_DIRECTORS_FROM_MOVIES = " DELETE FROM films_directors WHERE film_id = ? ";
    private static final String EXISTS_LIKE = "SELECT EXISTS(SELECT 1 FROM films_like WHERE film_id = ? AND user_id = ?) ";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, GenreRowMapper genreMapper,
                         MPARowMapper mpaMapper) {
        super(jdbc, mapper);
        this.genreMapper = genreMapper;
        this.mpaMapper = mpaMapper;
    }

    @Override
    public Collection<Film> getFilms() {
        return getAll(GET_ALL_FILMS);
    }

    @Override
    public Film postFilm(Film film) {
        long id = post(
                INSERT_FILM,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                (int) film.getDuration().toSeconds(),
                film.getMpa().getId()
        );
        film.setId(id);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            film.getGenres()
                    .forEach(genre ->
                            jdbc.update(ADD_IN_FILMS_GENRES, film.getId().intValue(), genre.getId().intValue()));

        }
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            film.getDirectors()
                    .forEach(director ->
                            jdbc.update(ADD_IN_FILMS_DIRECTOR, film.getId().intValue(), director.getId().intValue()));
        }

        return film;
    }

    @Override
    public void addInTableGenresAndFilms(long idFilm, List<Long> idsGenres) {
        jdbc.batchUpdate(ADD_IN_FILMS_GENRES, idsGenres, idsGenres.size(),
                (ps, genreId) -> {
                    ps.setLong(1, idFilm);
                    ps.setLong(2, genreId);
                });
    }

    @Override
    public void deleteGenresFromMovie(long filmId) {
        delete(DELETE_GENRE_FROM_MOVIES, filmId);
    }

    @Override
    public Film putFilm(Film film) {
        Map<String, Object> fields = new LinkedHashMap<>();
        if (film.getName() != null) {
            fields.put("name", film.getName());
        }
        if (film.getDescription() != null) {
            fields.put("description", film.getDescription());
        }
        if (film.getReleaseDate() != null) {
            fields.put("release_date", Date.valueOf(film.getReleaseDate()));
        }
        if (film.getDuration() != null) {
            fields.put("duration", (int) film.getDuration().toSeconds());
        }
        if (film.getMpa() != null) {
            fields.put("mpa_id", film.getMpa().getId());
        }
        if (!film.getGenres().isEmpty()) {
            deleteGenresFromMovie(film.getId());
            addInTableGenresAndFilms(film.getId(), film.getGenres().stream().map(Genre::getId).toList());
        } else {
            delete(DELETE_GENRE_FROM_MOVIES, film.getId());
        }

        if (fields.isEmpty()) {
            throw new IllegalArgumentException();
        }
        update(UPDATE_FILM, fields, film.getId());
        if (!film.getDirectors().isEmpty()) {
            delete(DELETE_DIRECTORS, film.getId());

            film.getDirectors().forEach(director ->
                    jdbc.update(ADD_IN_FILMS_DIRECTOR, film.getId(), director.getId())
            );
        } else {
            delete(DELETE_DIRECTORS_FROM_MOVIES, film.getId());
        }
        return film;
    }

    @Override
    public void removeFilm(long filmId) {
        delete(REMOVE_FILM, filmId);
    }

    @Override
    public Optional<Film> findFilmById(long id) {
        return getOneById(GET_FILM_BY_ID, id);
    }

    @Override
    public boolean addLike(long id, long idUser) {
        boolean isExists = jdbc.queryForObject(EXISTS_LIKE, Boolean.class, id, idUser);
        postEvent(idUser, EventType.LIKE, Operation.ADD, id);
        if (isExists) {
            return false;
        } else {
            jdbc.update(ADD_LIKE, id, idUser);
            return true;
        }


    }

    @Override
    public Set<Long> getFilmsLikes(long id) {
        return new HashSet<>(jdbc.queryForList(GET_LIKES_FOR_FILM, Long.class, id));
    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        return queryForLst(GET_TOP_FILMS_LIMITED, count);
    }

    @Override
    public List<Film> getTopFilmsByGenreAndYear(Integer count, Integer genreId, Integer year) {
        return getAll(GET_TOP_FILMS_BY_GENRE_AND_YEAR_LIMITED, genreId, year, count);
    }

    @Override
    public List<Film> getTopFilmsByGenre(Integer count, Integer genreId) {
        log.info(getAll(GET_TOP_FILMS_BY_GENRE_LIMITED, genreId, count).toString());
        return getAll(GET_TOP_FILMS_BY_GENRE_LIMITED, genreId, count);
    }

    @Override
    public List<Film> getTopFilmsByYear(Integer count, Integer year) {
        return getAll(GET_TOP_FILMS_BY_YEAR_LIMITED, year, count);
    }

    @Override
    public void removeLike(long idFilm, long idUser) {
        remove(REMOVE_LIKE, idFilm, idUser);
        postEvent(idUser, EventType.LIKE, Operation.REMOVE, idFilm);
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbc.query(GET_ALL_GENRES, genreMapper);
    }

    @Override
    public Optional<Genre> getGenreById(long id) {
        try {
            Genre genre = jdbc.queryForObject(GET_GENRE_BY_ID, genreMapper, id);
            return Optional.ofNullable(genre);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public List<MPA> getAllMpa() {
        return jdbc.query(GET_ALL_MPA, mpaMapper);
    }

    @Override
    public Optional<MPA> getMpaById(long id) {
        try {
            MPA mpa = jdbc.queryForObject(GET_MPA_BY_ID, mpaMapper, id);
            return Optional.ofNullable(mpa);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> getGenresByIds(List<Long> ids) {
        String pHolders = ids.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));
        String query = "SELECT * " +
                "FROM genres " +
                "WHERE id IN ( " +
                pHolders + " )";
        return jdbc.query(query, genreMapper, ids.toArray());
    }

    @Override
    public boolean isExistFilmById(long id) {
        return isExistById(EXIST, id);
    }

    @Override
    public List<Film> getAllFilmsByDirectorSortByYear(long id) {
        return queryForLst(GET_FILMS_BY_DIRECTOR_SORT_BY_YEAR, id);
    }

    @Override
    public List<Film> getAllFilmsByDirectorSortByLikes(long id) {
        return queryForLst(GET_FILMS_BY_DIRECTOR_SORT_BY_LIKE, id);
    }

    @Override
    public List<Film> searchFilmsByTitle(String query) {
        return getAll(SEARCH_FILMS_BY_TITLE, query);
    }

    @Override
    public List<Film> searchFilmsByDirector(String query) {
        return getAll(SEARCH_FILMS_BY_DIRECTOR, query);
    }

    @Override
    public List<Film> searchFilmsByTitleAndDirector(String query) {
        return getAll(SEARCH_FILMS_BY_TITLE_AND_DIRECTOR, query, query);
    }

    @Override
    public Collection<Film> getCommonFilms(int userId, int friendId) {
        return getAll(GET_COMMON_FILMS, userId, friendId);
    }
}
