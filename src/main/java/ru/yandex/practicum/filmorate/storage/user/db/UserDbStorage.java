package ru.yandex.practicum.filmorate.storage.user.db;


import lombok.extern.slf4j.Slf4j;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;

import java.util.*;

@Slf4j

@Repository
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {

    private static final String GET_ALL_USERS = "SELECT * FROM users";
    private static final String INSERT_USER = "INSERT INTO users (name,email,login,birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE users SET ";
    private static final String GET_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String REMOVE_USER_BY_ID = "DELETE FROM friends WHERE (user_id = ? AND friend_id = ?) ";
    private static final String REMOVE_USER = "DELETE FROM users WHERE id = ?";

    private static final String GET_ALL_FRIENDS_BY_ID = "SELECT * " +
            "FROM users AS u " +
            "INNER JOIN friends AS f ON f.friend_id = u.id " +
            "WHERE f.user_id = ?";
    private static final String EXIST = "SELECT EXISTS(SELECT 1 FROM users WHERE id = ?) ";
    private static final String ADD_IN_FRIEND = "INSERT INTO friends (user_id,friend_id) VALUES (?, ?)";
    private static final String GET_COMMON_FRIENDS = "SELECT * " +
            "FROM users AS u " +
            "WHERE id  IN (SELECT friend_id FROM friends WHERE user_id = ?) " +
            "AND id IN (SELECT friend_id FROM friends WHERE user_id = ?)";
    private static final String GET_LIST_FRIENDS = "SELECT COUNT(*) " +
            "FROM friends " +
            "WHERE user_id = ?";
    private static final String QUERY_FOR_FRIEND = "SELECT COUNT(*) " +
            "FROM friends " +
            "WHERE (user_id = ? AND friend_id = ?) ";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<User> getUsers() {
        return getAll(GET_ALL_USERS);

    }

    @Override
    public User postUser(User user) {
        long id = post(
                INSERT_USER,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                Date.valueOf(user.getBirthday()));
        user.setId(id);
        return user;
    }

    @Override
    public User putUser(User user) {
        Map<String, Object> fields = new LinkedHashMap<>();
        if (user.getName() != null) {
            fields.put("name", user.getName());
        }
        if (user.getEmail() != null) {
            fields.put("email", user.getEmail());

        }
        if (user.getLogin() != null) {
            fields.put("login", user.getLogin());
        }
        if (user.getBirthday() != null) {
            fields.put("birthday", Date.valueOf(user.getBirthday()));
        }
        if (fields.isEmpty()) {
            throw new IllegalArgumentException();
        }
        update(UPDATE_USER, fields, user.getId());
        return user;
    }

    @Override
    public void removeUser(long userId) {
        delete(REMOVE_USER, userId);
    }

    @Override
    public Optional<User> getUserById(long id) {
        return getOneById(GET_USER_BY_ID, id);

    }

    @Override
    public boolean exists(long id) {
        return isExistById(EXIST, id);
    }

    @Override
    public void addInFriends(long userId, long friendId) {

        jdbc.update(ADD_IN_FRIEND, userId, friendId);

    }

    @Override
    public List<User> getAllFriendsOfUserById(long id) {
        return queryForLst(GET_ALL_FRIENDS_BY_ID, id);

    }

    @Override
    public Set<User> getCommonFriends(long userId, long friendId) {

        return new HashSet<>(jdbc.query(GET_COMMON_FRIENDS, mapper, userId, friendId));
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        remove(REMOVE_USER_BY_ID, userId, friendId);
    }

    @Override
    public boolean isExistListOfFriends(long id) {
        Integer count = jdbc.queryForObject(GET_LIST_FRIENDS, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public boolean haveUserFriend(long first, long second) {
        Integer count = jdbc.queryForObject(QUERY_FOR_FRIEND, Integer.class, first, second);
        return count != null && count > 0;
    }


}
