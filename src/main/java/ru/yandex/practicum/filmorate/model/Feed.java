package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class Feed {
    private Long eventId;
    private Long userId;
    private Long timestamp;
    private EventType eventType;
    private Operation operation;
    private Long entityId;

    public Feed(Long eventId, Long timestamp, Long userId, EventType eventType, Operation operation, Long entityId) {
        this.eventId = eventId;
        this.timestamp = timestamp;
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
    }
}
