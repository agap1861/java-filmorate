package ru.yandex.practicum.filmorate.exception;


public class NotFoundException extends RuntimeException {
    private long id;

    public NotFoundException(String message) {
        super(message);
    }

}
