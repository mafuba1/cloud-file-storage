package ru.nasrulaev.cloudfilestorage.controllers;

import io.minio.errors.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.nasrulaev.cloudfilestorage.dto.FileUpload;
import ru.nasrulaev.cloudfilestorage.security.PersonDetails;
import ru.nasrulaev.cloudfilestorage.services.UserFilesService;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

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
        model.addAttribute("currentUrl", "/tree/");
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
        model.addAttribute("currentUrl", "/tree/" + subFolder);
        model.addAttribute(
                "files",
                userFilesService.listFolder(
                        personDetails.person(),
                        subFolder
                )
        );
        return "files/tree";
    }

    @PostMapping({"", "/"})
    public ResponseEntity<HttpStatus> uploadFile(@AuthenticationPrincipal PersonDetails personDetails,
                                                 @ModelAttribute("files") FileUpload fileUpload) {
        List<MultipartFile> multipartFiles = fileUpload.getFiles();
        userFilesService.putObjects(personDetails.person(), "", multipartFiles);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("**")
    public ResponseEntity<HttpStatus> uploadFile(@AuthenticationPrincipal PersonDetails personDetails,
                                                 @ModelAttribute("files") FileUpload fileUpload,
                                                 HttpServletRequest request) {
        List<MultipartFile> multipartFiles = fileUpload.getFiles();
        final String folder = extractSubRequest(request);
        userFilesService.putObjects(personDetails.person(), folder, multipartFiles);
        return ResponseEntity.ok(HttpStatus.OK);
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
