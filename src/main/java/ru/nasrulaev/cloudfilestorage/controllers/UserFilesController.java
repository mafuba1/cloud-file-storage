package ru.nasrulaev.cloudfilestorage.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.nasrulaev.cloudfilestorage.security.PersonDetails;
import ru.nasrulaev.cloudfilestorage.services.UserFilesService;

@Controller
@RequestMapping("/tree")
public class UserFilesController {

    private final UserFilesService userFilesService;

    @Autowired
    public UserFilesController(UserFilesService userFilesService) {
        this.userFilesService = userFilesService;
    }

    @GetMapping({"", "/"})
    public String rootFolder(@AuthenticationPrincipal PersonDetails personDetails,
                             Model model) {
        model.addAttribute(
                "files",
                userFilesService.listFolder(
                        personDetails.person(),
                        ""
                )
        );
        return "files/tree";
    }

    @GetMapping("**")
    public String subFolder(@AuthenticationPrincipal PersonDetails personDetails,
                             HttpServletRequest request, Model model) {
        final String subFolder = request.getRequestURI()
                .split(request.getContextPath() + "/tree/")[1];
        model.addAttribute(
                "files",
                userFilesService.listFolder(
                        personDetails.person(),
                        subFolder
                )
        );
        return "files/tree";
    }
}
