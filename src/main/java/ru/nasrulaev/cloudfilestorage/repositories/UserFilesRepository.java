package ru.nasrulaev.cloudfilestorage.repositories;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public void putObject(String objectName, InputStream stream) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(ROOT_BUCKET)
                        .object(objectName)
                        .stream(stream, -1, 10485760)
                        .build()
        );
    }

    public void createFolder(String folder) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(ROOT_BUCKET)
                        .object(folder)
                        .stream(new ByteArrayInputStream(new byte[] {}), 0, -1)
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

    public void removeFolder(String folderName) {
        this.listFolder(folderName).forEach(
                itemResult -> {
                    try {
                        this.removeObject(itemResult.get().objectName());
                    }
                    catch (ErrorResponseException | InsufficientDataException | InternalException |
                           InvalidKeyException | InvalidResponseException | IOException | NoSuchAlgorithmException |
                           ServerException | XmlParserException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }


}
