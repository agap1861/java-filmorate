package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.BadInsertException;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BaseDbStorage<T> {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    protected Optional<T> getOneById(String query, long id) {
        try {
            T result = jdbc.queryForObject(query, mapper, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected List<T> getAll(String query) {
        return jdbc.query(query, mapper);
    }

    protected long post(String query, Object... params) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps;
        }, generatedKeyHolder);
        Number id = generatedKeyHolder.getKey();
        if (id != null) {
            return id.longValue();
        } else {
            throw new BadInsertException("Unable to save data");
        }
    }

    protected void update(String query, Map<String, Object> fields, long id) {
        String set = fields.keySet().stream()
                .map(o -> o + " = ?")
                .collect(Collectors.joining(", "));
        String result = query + set + " WHERE id = ?";
        List<Object> updates = new ArrayList<>(fields.values());
        updates.add(id);
        int rowsUpdated = jdbc.update(result, updates.toArray());
        if (rowsUpdated == 0) {
            throw new BadInsertException("Unable to update data");
        }


    }

    protected void remove(String query, long id_1, long id_2) {
        jdbc.update(query, id_1, id_2);
    }

    protected List<T> queryForLst(String query, long id) {
        return jdbc.query(query, mapper, id);
    }

    protected boolean isExistById(String query, long id) {
        Integer count = jdbc.queryForObject(query, Integer.class, id);
        return count != null && count > 0;

    }


}
