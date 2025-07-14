package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Data;


import java.time.Duration;
import java.time.LocalDate;


@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Duration duration;

    public Film(Long id, String name, String description, LocalDate releaseDate, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    @JsonGetter("duration")
    public long getDurationInMinutes() {
        return duration.toSeconds();
    }

}
