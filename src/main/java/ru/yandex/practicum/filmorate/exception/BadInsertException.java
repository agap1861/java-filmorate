package ru.yandex.practicum.filmorate.exception;

public class BadInsertException extends RuntimeException {
    public BadInsertException(String message) {
        super(message);
    }
}
