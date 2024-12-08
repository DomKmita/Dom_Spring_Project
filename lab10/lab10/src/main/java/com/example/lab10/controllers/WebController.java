package com.example.lab10.controllers;

import com.example.lab10.dtos.HouseholdData;
import com.example.lab10.dtos.PetData;
import com.example.lab10.entities.Household;
import com.example.lab10.entities.Pet;
import com.example.lab10.services.HouseholdService;
import com.example.lab10.services.PetService;
import com.example.lab10.exceptions.NotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class WebController {

    private final HouseholdService householdService;
    private final PetService petService;

    @GetMapping("/pets")
    public List<Pet> getAllPets() {
       return petService.getAllPets();
    }

    @GetMapping("/households")
    public List<Household> getAllHouseholds() {
       return householdService.getAllHouseholds();
    }

    // Get households with no pets
    @GetMapping("/households/no-pets")
    public List<Household> getHouseholdsWithNoPets() {
        return householdService.findHouseholdsWithNoPets();
    }

    // Get a specific pet
    @GetMapping("/pets/{id}")
    public Pet getPetById(@PathVariable int id) throws NotFoundException {
        return petService.getPetById(id);
    }

    // Get a specific household
    @GetMapping("/households/{eircode}")
    public Household getHouseholdByEircode(@PathVariable String eircode) throws NotFoundException {
       return householdService.findHouseholdByEircode(eircode);
    }

    // Delete a pet
    @DeleteMapping("/pets/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePet(@PathVariable int id) throws NotFoundException {
        petService.deletePetById(id);
    }

    // Delete a household
    @DeleteMapping("/households/{eircode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHousehold(@PathVariable String eircode) throws NotFoundException {
        householdService.deleteHousehold(eircode);
    }

    // Create a new household
    @PostMapping("/households")
    @ResponseStatus(HttpStatus.CREATED)
    public void createHousehold(@Valid @RequestBody HouseholdData householdData) {
        Household newHousehold = new Household(
                householdData.eircode(),
                householdData.numberOfOccupants(),
                householdData.maxNumberOfOccupants(),
                householdData.ownerOccupied(),
                null);

        householdService.saveHousehold(newHousehold);

    }

    // Create a new pet
    @PostMapping("/pets")
    @ResponseStatus(HttpStatus.CREATED)
    public void createPet(@Valid @RequestBody PetData petData) throws NotFoundException {
        Household household = householdService.findHouseholdByEircode(petData.eircode());
        Pet newPet = new Pet(
                petData.name(),
                petData.animalType(),
                petData.breed(),
                petData.age(),
                household);
        petService.createPet(newPet);
    }

    // Change a pet's name
    @PatchMapping("/pets/{id}")
    public Pet changePetName(@PathVariable int id, @RequestParam("name") String name) throws NotFoundException {
        Pet pet = petService.getPetById(id);
        pet.setName(name);
        return petService.updatePet(id, pet);
    }
}
