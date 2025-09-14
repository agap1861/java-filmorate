package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;


@Data
@NoArgsConstructor
public class MPA {
    private String name;
    private long id;

    @Autowired
    public MPA(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
