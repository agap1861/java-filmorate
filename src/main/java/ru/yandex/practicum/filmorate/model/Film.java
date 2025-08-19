package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Data;


import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Duration duration;
    private MPA mpa;
    private List<Genre> genres;


    public Film(Long id, String name, String description, LocalDate releaseDate, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
    public Film(){};

    @JsonGetter("duration")
    public long getDurationInMinutes() {
        return duration.toSeconds();
    }

}
