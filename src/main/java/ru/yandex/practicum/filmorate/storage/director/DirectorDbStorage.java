package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class DirectorDbStorage extends BaseDbStorage<Director> implements DirectorStorage {


    private final static String GET_ALL_DIRECTORS = "SELECT * FROM directors";
    private final static String GET_DIRECTOR_BY_ID = "SELECT * FROM directors WHERE id = ?";
    private final static String INSERT_DIRECTOR = "INSERT INTO directors (name) VALUES (?)";
    private final static String UPDATE_DIRECTOR = "UPDATE directors SET ";
    private final static String DELETE_DIRECTOR = "DELETE FROM directors WHERE id = ? ";
    private final static String DELETE_DIRECTOR_RELATIONS = "DELETE FROM films_directors WHERE director_id = ? ";
    private static final String EXIST = "SELECT EXISTS(SELECT 1 FROM directors WHERE id = ?) ";

    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Director> getAllDirectors() {

        return getAll(GET_ALL_DIRECTORS);

    }

    @Override
    public Optional<Director> getDirectorById(long id) {
        return getOneById(GET_DIRECTOR_BY_ID, id);
    }

    @Override
    public Director postDirector(Director director) {
        long id = post(INSERT_DIRECTOR,
                director.getName());
        director.setId(id);
        return director;
    }

    @Override
    public Director putDirector(Director director) {
        Map<String, Object> fields = new LinkedHashMap<>();
        if (director.getName() != null) {
            fields.put("name", director.getName());
        }
        update(UPDATE_DIRECTOR, fields, director.getId());
        return director;
    }

    @Override
    public void deleteDirector(long id) {
        delete(DELETE_DIRECTOR_RELATIONS, id);
        delete(DELETE_DIRECTOR, id);
    }

    @Override
    public boolean isExistDirectorById(long id) {
        return isExistById(EXIST, id);

    }

    @Override
    public List<Director> getDirectorsByIds(List<Long> ids) {
        String pHolders = ids.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));
        String query = "SELECT * " +
                "FROM directors " +
                "WHERE id IN ( " +
                pHolders + " )";
        return jdbc.query(query, mapper, ids.toArray());
    }


}
