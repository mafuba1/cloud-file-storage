package ru.nasrulaev.cloudfilestorage.controllers;

import io.minio.errors.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.nasrulaev.cloudfilestorage.security.PersonDetails;
import ru.nasrulaev.cloudfilestorage.services.UserFilesService;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
        final String subFolder = extractSubRequest(request);
        model.addAttribute(
                "files",
                userFilesService.listFolder(
                        personDetails.person(),
                        subFolder
                )
        );
        return "files/tree";
    }


    @DeleteMapping("**")
    public String removeObject(@AuthenticationPrincipal PersonDetails personDetails,
                            HttpServletRequest request) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        final String pathToObject = extractSubRequest(request);
        userFilesService.removeObject(personDetails.person(), pathToObject);
        return "redirect:/tree/" + extractCurrentFolder(pathToObject);
    }

    private String extractSubRequest(HttpServletRequest request) {
        return request.getRequestURI()
                .split(request.getContextPath() + "/tree/")[1];
    }

    private String extractCurrentFolder(String objectPath) {
        if (objectPath.endsWith("/")) objectPath = objectPath.replaceAll(".$", "");
        if (!objectPath.contains("/")) return "";
        return objectPath.substring(0, objectPath.lastIndexOf("/"));
    }
}
