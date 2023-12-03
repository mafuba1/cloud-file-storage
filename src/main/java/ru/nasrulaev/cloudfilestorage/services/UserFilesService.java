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

    public List<String> listFolder(Person person, String path) {
        String userFolder = getUserFolder(person);
        String fullPath = trueFolder(userFolder + path);
        Iterable<Result<Item>> results = userFilesRepository.listFolder(fullPath, false);
        List<String> fileList = new ArrayList<>();
        results.forEach(
                itemResult ->
                {
                    try {
                        fileList.add(itemResult.get().objectName().substring(userFolder.length()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

        );
        return fileList;
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

    public void removeObject(Person person, String objectPath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String fullPath = getUserFolder(person) + objectPath;
        if (fullPath.endsWith("/")) {
            userFilesRepository.removeFolder(fullPath);
        } else {
            userFilesRepository.removeObject(fullPath);
        }
    }

    private String getUserFolder(Person person) {
        return "user-" + person.getId() + "-files/";
    }

    private String trueFolder(String folder) {
         return folder.endsWith("/") ? folder : folder + "/";
    }

}
