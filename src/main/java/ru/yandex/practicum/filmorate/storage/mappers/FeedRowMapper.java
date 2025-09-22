package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FeedRowMapper implements RowMapper<Feed> {
    @Override
    public Feed mapRow(ResultSet rs, int rowNum) throws SQLException {
        Feed feed = new Feed();
        feed.setEventId(rs.getLong("event_id"));
        feed.setTimestamp(rs.getLong("timestamp"));
        feed.setUserId(rs.getLong("user_id"));
        String event = rs.getString("event_type");
        feed.setEventType(EventType.valueOf(event));
        String operation = rs.getString("operation");
        feed.setOperation(Operation.valueOf(operation));
        feed.setEntityId(rs.getLong("entity_id"));
        return feed;
    }
}
