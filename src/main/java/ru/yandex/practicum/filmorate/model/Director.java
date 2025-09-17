package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Director {
    Long id;
    @NotNull
    String name;

    public Director(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
