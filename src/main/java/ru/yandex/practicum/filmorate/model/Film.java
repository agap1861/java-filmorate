package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.Duration;
import java.time.LocalDate;

import java.util.List;


@Data
@NoArgsConstructor
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Duration duration;
    private MPA mpa;
    private List<Genre> genres;
    private List<Director> directors;

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
