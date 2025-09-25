package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(Duration.ofSeconds(rs.getLong("duration")));
        film.setMpa(new MPA(rs.getLong("mpa_id"), rs.getString("mpa_name")));
        if (rs.getString("genre_names") != null && rs.getString("genres_id") != null) {
            String[] names = rs.getString("genre_names").split(",");
            String[] id = rs.getString("genres_id").split(",");
            Set<Genre> genres = new HashSet<>();
            if (names.length != 0 && id.length != 0) {
                for (int i = 0; i < id.length; i++) {
                    genres.add(new Genre(Long.parseLong(id[i]), names[i]));
                }

            }
            film.setGenres(genres.stream().toList());

        } else {
            film.setGenres(List.of());
        }
        if (rs.getString("director_names") != null && rs.getString("directors_id") != null) {
            String[] names = rs.getString("director_names").split(",");
            String[] id = rs.getString("directors_id").split(",");
            Set<Director> directors = new HashSet<>();
            if (names.length != 0 && id.length != 0) {
                for (int i = 0; i < id.length; i++) {
                    directors.add(new Director(Long.parseLong(id[i]), names[i]));
                }
            }
            film.setDirectors(directors.stream().toList());

        } else {
            film.setDirectors(List.of());
        }

        return film;


    }

}
