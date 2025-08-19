package ru.yandex.practicum.filmorate.storage.user.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final UserRowMapper mapper;

    @Override
    public Collection<User> getUsers() {
        String query = "SELECT * FROM users";
        return jdbc.query(query, mapper);
    }

    @Override
    public User postUser(User user) {
        String sql = "INSERT INTO users (name,email,login,birthday) VALUES (?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getLogin());
            statement.setDate(4, Date.valueOf(user.getBirthday()));
            return statement;
        }, keyHolder);
        Number id = keyHolder.getKey();
        if (id != null) {
            user.setId(id.longValue());
            return user;
        } else {
            throw new InternalServerException("Unable to save data");
        }

    }

    @Override
    public User putUser(User user) {

        List<Object> fields = new ArrayList<>();
        List<String> update = new ArrayList<>();
        if (user.getName() != null) {

            fields.add(user.getName());
            update.add("name = ?");
        }
        if (user.getEmail() != null) {

            fields.add(user.getEmail());
            update.add("email = ?");
        }
        if (user.getLogin() != null) {

            fields.add(user.getLogin());
            update.add("login = ?");
        }
        if (user.getBirthday() != null) {

            fields.add(Date.valueOf(user.getBirthday()));
            update.add("birthday = ?");
        }
        if (fields.isEmpty()) {
            throw new IllegalArgumentException();
        }
        String result = "UPDATE users SET " + String.join(", ", update) + " WHERE id = ?";
        fields.add(user.getId());

        int rowsUpdated = jdbc.update(result, fields.toArray());
        if (rowsUpdated == 0) {
            throw new InternalServerException("Unable to update data");
        }
        return user;


    }

    @Override
    public Optional<User> getUserById(long id) {
        String query = "SELECT * FROM users WHERE id = ?";
        try {
            User user = jdbc.queryForObject(query, mapper, id);
            return Optional.ofNullable(user);

        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }

    }

    @Override
    public boolean exists(long id) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public void addInFriends(long userId, long friendId) {
        String queryAdd = "INSERT INTO friends (user_id,friend_id) VALUES (?, ?)";
        jdbc.update(queryAdd, userId, friendId);

    }

    @Override
    public List<User> getAllFriendsOfUserById(long id) {
        String query = "SELECT * " +
                "FROM users AS u " +
                "INNER JOIN friends AS f ON f.friend_id = u.id " +
                "WHERE f.user_id = ?";
        return jdbc.query(query, mapper, id);

    }

    @Override
    public Set<User> getCommonFriends(long userId, long friendId) {
        String query = "SELECT * " +
                "FROM users AS u " +
                "WHERE id  IN (SELECT friend_id FROM friends WHERE user_id = ?) " +
                "AND id IN (SELECT friend_id FROM friends WHERE user_id = ?)";
        return new HashSet<>(jdbc.query(query, mapper, userId, friendId));
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        log.info("Список друзей пользователя " + userId + Arrays.toString(getAllFriendsOfUserById(userId).toArray()));
        String query = "DELETE " +
                "FROM friends " +
                "WHERE (user_id = ? AND friend_id = ?) ";
        jdbc.update(query, userId, friendId);
        log.info("Список друзей пользователя " + userId + Arrays.toString(getAllFriendsOfUserById(userId).toArray()));

    }

    @Override
    public boolean isExistListOfFriends(long id) {
        String query = "SELECT COUNT(*) " +
                "FROM friends " +
                "WHERE user_id = ?";
        Integer count = jdbc.queryForObject(query, Integer.class, id);
        return count != null && count > 0;

    }

    @Override
    public boolean haveUserFriend(long first, long second) {
        String query = "SELECT COUNT(*) " +
                "FROM friends " +
                "WHERE (user_id = ? AND friend_id = ?) ";
        log.info(getAllFriendsOfUserById(first).toString());

        Integer count = jdbc.queryForObject(query, Integer.class, first, second);
        return count != null && count > 0;

    }


}
