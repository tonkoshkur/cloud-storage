package ua.tonkoshkur.cloudstorage.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    private String url;
    private String accessKey;
    private String accessSecret;
    private String bucketName;
    private String userFolderFormat;
}
