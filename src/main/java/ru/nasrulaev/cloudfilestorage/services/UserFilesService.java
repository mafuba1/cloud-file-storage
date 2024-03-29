package ru.nasrulaev.cloudfilestorage.services;

import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.nasrulaev.cloudfilestorage.models.Person;
import ru.nasrulaev.cloudfilestorage.repositories.UserFilesRepository;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserFilesService {

    private final UserFilesRepository userFilesRepository;


    @Autowired
    public UserFilesService(UserFilesRepository userFilesRepository) {
        this.userFilesRepository = userFilesRepository;
    }

    public List<String> listFolder(Person person, String path, boolean recursive) {
        String userFolder = getUserFolder(person);
        String fullPath = userFolder + path;
        Iterable<Result<Item>> results = userFilesRepository.listFolder(fullPath, recursive);
        return resultsToList(person, results);
    }

    public List<String> searchObject(Person person, String objectName) {
        List<String> searchResult = listFolder(
                person,
                objectName,
                false
        );

        listFolder(person, extractParentFolder(objectName), false).stream()
                .filter(name ->
                        name.endsWith("/")
                )
                .forEach(newFolder ->
                        searchResult.addAll(
                                searchObject(person, newFolder + objectName)
                        )
                );

        return searchResult;
    }

    public InputStream getObject(Person person, String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String fullPath = getUserFolder(person) + path;
        return userFilesRepository.getObject(fullPath);
    }

    public void putObjects(Person person, String folder, List<MultipartFile> multipartFiles) {
        String userFolder = getUserFolder(person);
        multipartFiles.stream()
                .filter(multipartFile -> !multipartFile.isEmpty())
                .forEach(multipartFile ->
                {
                    try {
                        userFilesRepository.putObject(
                                userFolder + trueFolder(folder) + multipartFile.getOriginalFilename(),
                                multipartFile.getInputStream()
                        );
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    public void createFolder(Person person, String folder, String folderName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String userFolder = getUserFolder(person);
        System.out.println(folder);
        System.out.println(folderName);
        System.out.println(trueFolder(userFolder + folder) + trueFolder(folderName));
        userFilesRepository.createFolder(
                trueFolder(userFolder + folder) + trueFolder(folderName)
        );
    }

    public void renameObject(Person person, String oldName, String newName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        if (oldName.endsWith("/")) {
            String newPath = oldName.replaceFirst(
                    "[^/]+/$",
                    "/" + newName + "/"
            );
            renameFolder(person, oldName, newPath);
        }
        else {
            String newPath = oldName.replaceFirst(
                    "[^/]+$",
                    newName
            );
            renameFile(person, oldName, newPath);
        }
    }

    private void renameFile(Person person, String oldName, String newName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String oldPath = getUserFolder(person) + oldName;
        String newPath = getUserFolder(person) + newName;
        userFilesRepository.copyObject(oldPath, newPath);
        userFilesRepository.removeObject(oldPath);
    }

    private void renameFolder(Person person, String oldName, String newName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<String> objectsToRename = this.listFolder(person, oldName, true);
        for (String oldPath : objectsToRename) {
                this.renameFile(person, oldPath, oldPath.replace(oldName, newName));
        }
        this.removeObject(person, oldName);
    }

    public void removeObject(Person person, String objectPath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String fullPath = getUserFolder(person) + objectPath;
        if (fullPath.endsWith("/")) {
            userFilesRepository.removeFolder(fullPath);
        } else {
            userFilesRepository.removeObject(fullPath);
        }
    }

    private List<String> resultsToList(Person person, Iterable<Result<Item>> results){
        List<String> list = new ArrayList<>();
        String userFolder = getUserFolder(person);

        results.forEach(
                itemResult ->
                {
                    try {
                        list.add(itemResult.get().objectName().substring(userFolder.length()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

        );

        return list;
    }

    public String getUserFolder(Person person) {
        return "user-" + person.getId() + "-files/";
    }

    public String trueFolder(String folder) {
         return folder.endsWith("/") ? folder : folder + "/";
    }

    public String extractParentFolder(String objectPath) {
        if (objectPath.endsWith("/")) objectPath = objectPath.replaceAll(".$", "");
        if (!objectPath.contains("/")) return "";
        return objectPath.substring(0, objectPath.lastIndexOf("/"));
    }
}
