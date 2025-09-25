package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.director.DirectorService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService service;

    @GetMapping
    public List<Director> getAllDirectors() {
        return service.getAllDirectors();

    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable long id) {
        return service.getDirectorById(id);

    }

    @PostMapping()
    public Director postDirector(@RequestBody Director director) {
        return service.postDirector(director);

    }

    @PutMapping
    public Director putDirector(@RequestBody Director director) {
        return service.putDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable long id) {
        service.deleteDirector(id);
    }

}
