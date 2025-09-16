package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    List<Director> getAllDirectors();

    Optional<Director> getDirectorById(long id);

    Director postDirector(Director director);

    Director putDirector(Director director);

    void deleteDirector(long id);

    boolean isExistDirectorById(long id);

    List<Director> getDirectorsByIds(List<Long> ids);
}
