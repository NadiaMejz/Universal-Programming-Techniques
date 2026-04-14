package zad1.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import zad1.Models.Animal;
import zad1.Repositories.AnimalRepository;

import java.util.List;

@Controller
public class HtmlController {
    @Autowired
    private AnimalRepository animalRepository;

    @GetMapping("/web/list")
    public String getList(Model model){
        model.addAttribute("animals", animalRepository.findAll());
        return "list";
    }

    @GetMapping("/web/list/sorted/name")
    public String getSortedListName(Model model){
        List<Animal> sortedAnimals = animalRepository.findAllByOrderByNameAsc();
        model.addAttribute("animals", sortedAnimals);
        return "list";
    }
    @GetMapping("/web/list/sorted/age")
    public String getSortedListAge(Model model){
        List<Animal> sortedAnimals = animalRepository.findAllByOrderByAgeAsc();
        model.addAttribute("animals", sortedAnimals);
        return "list";
    }
    @GetMapping("/web/list/sorted/species")
    public String getSortedListSpecies(Model model){
        List<Animal> sortedAnimals = animalRepository.findAllByOrderBySpeciesAsc();
        model.addAttribute("animals", sortedAnimals);
        return "list";
    }

    @GetMapping("/web/add")
    public String showAddForm(Model model){
        model.addAttribute("animal", new Animal());
        return "form";
    }

    @PostMapping("/web/save")
    public String saveAnimal(@ModelAttribute Animal animal){
        animalRepository.save(animal);
        return "redirect:/web/list";
    }

    @GetMapping("/web/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model){
        Animal animal = animalRepository.findById(id).orElseThrow();
        model.addAttribute("animal", animal);
        return "form";
    }

    @GetMapping("/web/delete/{id}")
    public String deleteAnimal(@PathVariable Long id){
        animalRepository.deleteById(id);
        return "redirect:/web/list";
    }
}

