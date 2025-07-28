package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {
    private Map<Long, Set<Long>> friends = new HashMap<>();
    UserStorage storage;

    public UserService(InMemoryUserStorage storage) {
        this.storage = storage;
    }

    public void addInFriends(long userId, long friendId) {
        friends.computeIfAbsent(userId, key -> new HashSet<>()).add(friendId);
        friends.computeIfAbsent(friendId, key -> new HashSet<>()).add(userId);

    }

    public void removeFromFriends(long userId, long friendId) {

        if (storage.getUserById(userId).isEmpty() || storage.getUserById(friendId).isEmpty()){
            throw new NotFoundException("Not found");
        }
        if (!friends.containsKey(userId) || !friends.containsKey(friendId)) {
            return;
        }
        if (!friends.get(userId).contains(friendId) || !friends.get(friendId).contains(userId)) {
            throw new NotFoundException("Not found");
        }
        friends.get(userId).remove(friendId);
        friends.get(friendId).remove(userId);


    }

    public Set<Long> getAllFriendsOfUSerById(long id) {

        if (storage.getUserById(id).isEmpty()) {
            throw new NotFoundException("Not found");
        }
        if (!friends.containsKey(id)) {
            return Set.of();
        }

        return friends.get(id);

    }

    public Set<Long> getCommonFriends(long userId, long friendId) {
        Set<Long> userSet = friends.get(userId);
        Set<Long> friendSet = friends.get(friendId);

        if (userSet == null || friendSet == null) {
            throw new NotFoundException("Not found");
        }
        Set<Long> common = new HashSet<>();
        for (long id : userSet) {
            if (friendSet.contains(id)) {
                common.add(id);
            }
        }
        return common;

    }


}
