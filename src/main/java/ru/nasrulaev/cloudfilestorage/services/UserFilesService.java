package ru.nasrulaev.cloudfilestorage.services;

import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nasrulaev.cloudfilestorage.models.Person;
import ru.nasrulaev.cloudfilestorage.repositories.UserFilesRepository;

import java.io.IOException;
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
        String fullPath = userFolder + path;
        if (!fullPath.endsWith("/")) fullPath += "/";
        Iterable<Result<Item>> results = userFilesRepository.listFolder(fullPath);
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

    public void removeObject(Person person, String objectPath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String fullPath = getUserFolder(person) + objectPath;
        userFilesRepository.removeObject(fullPath);
    }

    private String getUserFolder(Person person) {
        return "user-" + person.getId() + "-files/";
    }

}
