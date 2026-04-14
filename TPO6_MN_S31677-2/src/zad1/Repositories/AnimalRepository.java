package zad1.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import zad1.Models.Animal;

import java.util.List;

public interface AnimalRepository extends JpaRepository<Animal,Long> {

List<Animal> findAllByOrderByAgeAsc();
    List<Animal> findAllByOrderBySpeciesAsc();
    List<Animal> findAllByOrderByNameAsc();

}
