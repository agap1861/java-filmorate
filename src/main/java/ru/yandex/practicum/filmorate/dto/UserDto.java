package ru.yandex.practicum.filmorate.dto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {

    private long id;
    private String login;
    private String name;
    private String email;
    private LocalDate birthday;


}
