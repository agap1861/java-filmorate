package ru.yandex.practicum.filmorate.model;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Director {
    Long id;
    String name;

    public Director(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
