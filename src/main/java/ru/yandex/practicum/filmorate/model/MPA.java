package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;


@Data

public class MPA {
    private String name;
    private long id;

    @Autowired
    public MPA(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public MPA() {
    }
}
