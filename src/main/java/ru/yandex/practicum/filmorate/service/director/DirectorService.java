package ru.yandex.practicum.filmorate.service.director;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Service

public class DirectorService {

    private DirectorStorage storage;

    public DirectorService(DirectorStorage storage) {
        this.storage = storage;
    }

    public List<Director> getAllDirectors() {
        return storage.getAllDirectors();
    }

    public Director getDirectorById(long id) {
        return storage.getDirectorById(id).orElseThrow(() -> new NotFoundException("Not found"));
    }

    public Director postDirector(Director director) {
        validateData(director);
        return storage.postDirector(director);
    }

    public Director putDirector(Director director) {
        validateData(director);
        if (storage.isExistDirectorById(director.getId()) && director.getName() != null) {
            return storage.putDirector(director);
        } else {
            throw new NotFoundException("not exist director");
        }

    }

    public void deleteDirector(long id) {
        if (storage.isExistDirectorById(id)) {
            storage.deleteDirector(id);
        }
    }

    public void validateData(Director director) {
        if (!StringUtils.hasText(director.getName())) {
            throw new ValidationException("name is empty");
        }
    }
}
