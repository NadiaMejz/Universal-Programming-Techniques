package zad1.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zad1.Models.Animal;
import zad1.Services.AnimalService;

import java.util.List;

@RestController
@RequestMapping("/data")
public class AnimalController {
    @Autowired
    private AnimalService service;

    @GetMapping("/all")
    public List<Animal> getAll() {
        return service.getAll();
    }

    @PostMapping("/insert")
    public void insert(@RequestBody Animal animal) {
        service.insert(animal);
    }

    @PutMapping("/update/{id}")
    public void update(@PathVariable Long id, @RequestBody Animal animal) {
        service.update(id, animal);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/all/age")
    public List<Animal> getAllByAge() {
        return service.getAllByAge();
    }

    @GetMapping("/all/species")
    public List<Animal> getAllBySpecies() {
        return service.getAllBySpecies();
    }

}
