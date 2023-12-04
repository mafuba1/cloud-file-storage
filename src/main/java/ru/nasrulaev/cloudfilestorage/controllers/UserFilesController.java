package ru.nasrulaev.cloudfilestorage.controllers;

import io.minio.errors.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.nasrulaev.cloudfilestorage.dto.FileUpload;
import ru.nasrulaev.cloudfilestorage.security.PersonDetails;
import ru.nasrulaev.cloudfilestorage.services.UserFilesService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Controller
public class UserFilesController {

    private final UserFilesService userFilesService;

    @Autowired
    public UserFilesController(UserFilesService userFilesService) {
        this.userFilesService = userFilesService;
    }

    @GetMapping("/tree/**")
    public String subFolder(@AuthenticationPrincipal PersonDetails personDetails,
                            HttpServletRequest request, Model model) {
        final String subFolder = extractSubRequest(request);
        final String parentFolder = extractParentFolder(subFolder);
        model.addAttribute("parentFolder", parentFolder);
        model.addAttribute("currentUrl", subFolder);

        List<String> userFiles = userFilesService.listFolder(
                personDetails.person(),
                subFolder,
                false
        );

        boolean selfPointingFolder = userFiles.get(0).equals(
                        userFilesService.trueFolder(subFolder)
        );
        if (!userFiles.isEmpty() && selfPointingFolder)
            userFiles = userFiles.stream()
                    .skip(1)
                    .toList();

        model.addAttribute(
                "files",
                userFiles
        );

        return "files/tree";
    }

    @GetMapping("/download/**")
    public void downloadFile(@AuthenticationPrincipal PersonDetails personDetails,
                             HttpServletRequest request,
                             HttpServletResponse response) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        final String path = extractSubRequest(request);
        InputStream object = userFilesService.getObject(personDetails.person(), path);
        OutputStream out = response.getOutputStream();
        IOUtils.copy(object, out);
        out.flush();
    }

    @PostMapping("/upload/**")
    public String uploadFile(@AuthenticationPrincipal PersonDetails personDetails,
                                                 @ModelAttribute("files") FileUpload fileUpload,
                                                 @RequestParam Optional<String> folderName,
                                                 HttpServletRequest request) throws InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException {
        final String folder = extractSubRequest(request);

        if (folderName.isPresent()) {
            userFilesService.createFolder(personDetails.person(), folder, folderName.get());
            return "redirect:/tree/" +  folder;
        }


        List<MultipartFile> multipartFiles = fileUpload.getFiles();
        userFilesService.putObjects(personDetails.person(), folder, multipartFiles);
        return "redirect:/tree/" +  folder;
    }

    @PostMapping("/rename/**")
    public String renameObject(@AuthenticationPrincipal PersonDetails personDetails,
                               @RequestParam String newName,
                               HttpServletRequest request) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        final String oldName = extractSubRequest(request);
        userFilesService.renameObject(personDetails.person(), oldName, newName);
        return "redirect:/tree/" + extractParentFolder(oldName);
    }

    @DeleteMapping("/delete/**")
    public String removeObject(@AuthenticationPrincipal PersonDetails personDetails,
                            HttpServletRequest request) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        final String pathToObject = extractSubRequest(request);
        userFilesService.removeObject(personDetails.person(), pathToObject);
        return "redirect:/tree/" + extractParentFolder(pathToObject);
    }

    private String extractSubRequest(HttpServletRequest request) {
        return request.getRequestURI()
                .replaceFirst(
                        request.getContextPath() + "/[^/]+/?",
                        ""
                );
    }

    private String extractParentFolder(String objectPath) {
        if (objectPath.endsWith("/")) objectPath = objectPath.replaceAll(".$", "");
        if (!objectPath.contains("/")) return "";
        return objectPath.substring(0, objectPath.lastIndexOf("/"));
    }
}
