package ru.nasrulaev.cloudfilestorage.services;

import io.minio.Result;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nasrulaev.cloudfilestorage.models.Person;
import ru.nasrulaev.cloudfilestorage.repositories.UserFilesRepository;

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
        String fullPath = "user-" + person.getId() + "-files/" + path;
        if (!fullPath.endsWith("/")) fullPath += "/";
        Iterable<Result<Item>> results = userFilesRepository.listFolder(fullPath);
        List<String> fileList = new ArrayList<>();
        results.forEach(
                itemResult ->
                {
                    try {
                        fileList.add(itemResult.get().objectName());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

        );
        return fileList;
    }

}
