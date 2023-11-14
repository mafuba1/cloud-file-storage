package ru.nasrulaev.cloudfilestorage.repositories;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
public class UserFilesRepository {

    private final MinioClient minioClient;

    private final String ROOT_BUCKET = "user-files";

    @Autowired
    public UserFilesRepository(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public Iterable<Result<Item>> listFolder(String path) {
        return minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(ROOT_BUCKET)
                        .prefix(path)
                        .build()
        );
    }

    public void removeObject(String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(ROOT_BUCKET)
                        .object(objectName)
                        .build()
        );
    }

}
