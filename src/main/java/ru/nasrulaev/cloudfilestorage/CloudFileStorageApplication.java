package ru.nasrulaev.cloudfilestorage;

import io.minio.MinioClient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CloudFileStorageApplication {

	@Value(value = "${minio.endpoint:localhost}")
	private String MINIO_ENDPOINT;

	@Value(value = "${minio.user}")
	private String MINIO_USER;

	@Value(value = "${minio.password}")
	private String MINIO_PASSWORD;

	public static void main(String[] args) {
		SpringApplication.run(CloudFileStorageApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public MinioClient minioClient() {
		return MinioClient.builder()
				.endpoint(MINIO_ENDPOINT)
				.credentials(MINIO_USER, MINIO_PASSWORD)
				.build();
	}
}
