package zad1.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zad1.Models.Animal;
import zad1.Repositories.AnimalRepository;

import java.util.List;

@Service
public class AnimalService {
    @Autowired
    private AnimalRepository repository;

    public List<Animal> getAll() {
        return repository.findAll();
    }

    public void insert(Animal animal) {
        repository.saveAndFlush(animal);

    }
    public void update(Long id,Animal animal){
        animal.setId(id);
        repository.saveAndFlush(animal);
    }

    public void delete(Long id){
        repository.deleteById(id);
    }
    public List<Animal> getAllByAge() {
        return repository.findAllByOrderByAgeAsc();
    }
    public List<Animal> getAllBySpecies() {
        return repository.findAllByOrderBySpeciesAsc();
    }

}
