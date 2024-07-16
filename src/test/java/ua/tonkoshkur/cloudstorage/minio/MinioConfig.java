package ua.tonkoshkur.cloudstorage.minio;

import io.minio.MinioClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MinIOContainer;

@TestConfiguration
public class MinioConfig {

    @Bean
    public MinIOContainer minioContainer() {
        return new MinIOContainer("minio/minio");
    }

    @Bean
    public MinioClient minioClient(MinIOContainer minioContainer) {
        return MinioClient
                .builder()
                .endpoint(minioContainer.getS3URL())
                .credentials(minioContainer.getUserName(), minioContainer.getPassword())
                .build();
    }
}
