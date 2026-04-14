package zad1.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Animal {
    @Id
    @GeneratedValue

    private Long id;
    private String name;
    private int age;
    private String species;


    public Animal() {

    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getSpecies() {
        return species;
    }

    public Animal(Long id, String name, int age, String species) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.species = species;


    }

}
