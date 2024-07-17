package ua.tonkoshkur.cloudstorage.minio;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!test")
@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient(MinioProperties minioProperties) {
        return MinioClient.builder()
                .credentials(minioProperties.getAccessKey(),
                        minioProperties.getAccessSecret())
                .endpoint(minioProperties.getUrl())
                .build();
    }
}
