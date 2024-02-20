package ru.nasrulaev.cloudfilestorage.repositories;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
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

    @PostConstruct
    public void makeRootBucket() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        boolean rootBucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(ROOT_BUCKET)
                        .build());
        if (rootBucketExists)
            return;

        minioClient.makeBucket(
                MakeBucketArgs.builder()
                        .bucket(ROOT_BUCKET)
                        .build()
        );
    }

    public Iterable<Result<Item>> listFolder(String path, boolean recursive) {
        return minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(ROOT_BUCKET)
                        .prefix(path)
                        .recursive(recursive)
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

    public InputStream getObject(String object) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(ROOT_BUCKET)
                        .object(object)
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
        this.listFolder(folderName, true).forEach(
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

    public void copyObject(String oldPath, String newPath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.copyObject(
                CopyObjectArgs.builder()
                        .bucket(ROOT_BUCKET)
                        .object(newPath)
                        .source(
                                CopySource.builder()
                                        .bucket(ROOT_BUCKET)
                                        .object(oldPath)
                                        .build()
                        )
                        .build()
        );
    }


}
