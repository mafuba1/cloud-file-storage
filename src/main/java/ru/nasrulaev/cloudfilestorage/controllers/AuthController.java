package ru.nasrulaev.cloudfilestorage.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.nasrulaev.cloudfilestorage.dto.PersonDTO;
import ru.nasrulaev.cloudfilestorage.models.Person;
import ru.nasrulaev.cloudfilestorage.services.PeopleService;

@Controller
public class AuthController {

    private final PeopleService peopleService;
    private final ModelMapper modelMapper;


    @Autowired
    public AuthController(PeopleService peopleService, ModelMapper modelMapper) {
        this.peopleService = peopleService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }


    @GetMapping("/register")
    public String registrationPage(@ModelAttribute("person") PersonDTO personDTO) {
        return "auth/registration";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("person") PersonDTO personDTO,
                           BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "/auth/registration";

        peopleService.save(mapFromDTO(personDTO));

        return "redirect:/login";
    }

    private Person mapFromDTO(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }
}
