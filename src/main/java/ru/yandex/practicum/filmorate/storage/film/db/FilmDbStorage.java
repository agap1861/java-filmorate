package ru.yandex.practicum.filmorate.storage.film.db;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.MPARowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;


@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmMapper;
    private final GenreRowMapper genreMapper;
    private final MPARowMapper mpaMapper;

    @Override
    public Collection<Film> getFilms() {
        String query = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                "f.mpa_id, m.name AS mpa_name " +
                "FROM films AS f " +
                "INNER JOIN  mpa AS m ON m.id=f.mpa_id";
        return jdbc.query(query, filmMapper);
    }

    @Override
    public Film postFilm(Film film) {
        String query = "INSERT INTO films (name,description,release_date,duration,mpa_id) VALUES (?, ?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setLong(4, (int) film.getDuration().toSeconds());
            statement.setLong(5, film.getMpa().getId());
            return statement;
        }, keyHolder);
        Number id = keyHolder.getKey();
        if (id != null) {
            film.setId(id.longValue());
            return film;
        } else {
            throw new InternalServerException("Unable to save data");
        }
    }

    @Override
    public Film putFilm(Film film) {
        List<Object> fields = new ArrayList<>();
        List<String> updates = new ArrayList<>();

        if (film.getName() != null) {
            fields.add(film.getName());
            updates.add("name = ?");
        }
        if (film.getDescription() != null) {
            fields.add(film.getDescription());
            updates.add("description = ?");
        }
        if (film.getReleaseDate() != null) {
            fields.add(Date.valueOf(film.getReleaseDate()));
            updates.add("release_date = ?");
        }
        if (film.getDuration() != null) {
            fields.add(film.getDuration().toMinutes());
            updates.add("duration = ?");
        }
        if (film.getMpa() != null) {
            fields.add(film.getMpa().getId());
            updates.add("mpa_id = ?");
        }
        if (fields.isEmpty()) {
            throw new IllegalArgumentException();
        }
        String query = "UPDATE films SET " + String.join(", ", updates) + " WHERE id = ?";
        fields.add(film.getId());

        int rowsUpdated = jdbc.update(query, fields.toArray());
        if (rowsUpdated == 0) {
            throw new InternalServerException("Unable to update data");
        }
        return film;

    }

    @Override
    public Optional<Film> findFilmById(long id) {
        String query = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                "f.mpa_id, m.name AS mpa_name " +
                "FROM films AS f " +
                "INNER JOIN  mpa AS m ON m.id=f.mpa_id " +
                "WHERE f.id = ?";
        try {
            Film film = jdbc.queryForObject(query, filmMapper, id);
            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public boolean addLike(long id, long idUser) {
        String query = "INSERT INTO films_like (film_id,user_id) VALUES (?, ?)";
        int rowsUpdate = jdbc.update(query, id, idUser);
        return rowsUpdate != 0;

    }

    @Override
    public Set<Long> getFilmsLikes(long id) {
        String query = "SELECT user_id " +
                "FROM films_like " +
                "WHERE film_id = ?";
        return new HashSet<>(jdbc.queryForList(query, Long.class, id));
    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        String query = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                "f.mpa_id, m.name AS mpa_name " +
                "FROM films AS f " +
                "LEFT OUTER JOIN films_like as fl ON fl.film_id = f.id " +
                "INNER JOIN  mpa AS m ON m.id=f.mpa_id " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ? ";

        return jdbc.query(query, filmMapper, count);
    }

    @Override
    public void removeLike(long idFilm, long idUser) {
        String query = "DELETE FROM films_like WHERE film_id = ? AND user_id = ?";
        jdbc.update(query, idFilm, idUser);

    }

    @Override
    public List<Genre> getAllGenres() {
        String query = "SELECT * " +
                "FROM genres " +
                "ORDER BY id";
        return jdbc.query(query, genreMapper);
    }

    @Override
    public Optional<Genre> getGenreById(long id) {
        String query = "SELECT * " +
                "FROM genres " +
                "WHERE id = ?";
        try {
            Genre genre = jdbc.queryForObject(query, genreMapper, id);
            return Optional.ofNullable(genre);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public List<MPA> getAllMpa() {
        String query = "SELECT * " +
                "FROM mpa "+
                "ORDER BY id";
        return jdbc.query(query, mpaMapper);
    }

    @Override
    public Optional<MPA> getMpaById(long id) {
        String query = "SELECT * " +
                "FROM mpa " +
                "WHERE id = ?";
        try {
            MPA mpa = jdbc.queryForObject(query, mpaMapper, id);
            return Optional.ofNullable(mpa);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

}
