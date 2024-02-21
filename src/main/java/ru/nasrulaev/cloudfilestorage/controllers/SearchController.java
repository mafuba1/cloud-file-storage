package ru.nasrulaev.cloudfilestorage.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.nasrulaev.cloudfilestorage.security.PersonDetails;
import ru.nasrulaev.cloudfilestorage.services.UserFilesService;

import java.util.Collections;
import java.util.Optional;

@Controller
@RequestMapping("/search")
public class SearchController {

    private final UserFilesService userFilesService;

    @Autowired
    public SearchController(UserFilesService userFilesService) {
        this.userFilesService = userFilesService;
    }

    @GetMapping()
    public String searchPage(@RequestParam(value = "q", required = false) Optional<String> searchQuery,
                             @AuthenticationPrincipal PersonDetails personDetails,
                             Model model) {
        searchQuery.ifPresent(s ->
                model.addAttribute(
                    "searchResult",
                    userFilesService.searchObject(
                            personDetails.person(),
                            s
                    )
                )
        );
        if (searchQuery.isEmpty() || searchQuery.get().isBlank())  {
            model.addAttribute(
                    "searchResult",
                    Collections.emptyList()
            );
        }

        return "files/search";
    }
}
