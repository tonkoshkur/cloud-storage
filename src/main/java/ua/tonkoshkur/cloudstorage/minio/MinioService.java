package ua.tonkoshkur.cloudstorage.minio;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @SneakyThrows
    @PostConstruct
    public void createBucketIfNotExists() {
        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());
        if (!bucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        }
    }

    @SneakyThrows
    public Iterable<Result<Item>> findAll(String prefix, boolean recursive) {
        return minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .recursive(recursive)
                .build());
    }

    @SneakyThrows
    public InputStreamResource download(String path) {
        GetObjectResponse object = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(path)
                .build());
        return new InputStreamResource(object);
    }

    @SneakyThrows
    public void createFolder(String path) {
        putObject(new ByteArrayInputStream(new byte[]{}), 0, path);
    }

    @SneakyThrows
    public void uploadFile(MultipartFile multipartFile, String path) {
        putObject(multipartFile.getInputStream(), multipartFile.getSize(), path);
    }

    @SneakyThrows
    private void putObject(InputStream stream, long objectSize, String path) {
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(path)
                .stream(stream, objectSize, -1)
                .build());
    }

    @SneakyThrows
    public void copy(String fromPath, String toPath) {
        minioClient.copyObject(CopyObjectArgs.builder()
                .bucket(bucketName)
                .object(toPath)
                .source(CopySource.builder()
                        .bucket(bucketName)
                        .object(fromPath)
                        .build())
                .build());
    }

    @SneakyThrows
    public void delete(String path) {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(path)
                .build());
    }

    @SneakyThrows
    public boolean exists(String path) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(path)
                    .build());
            return true;
        } catch (ErrorResponseException ex) {
            return false;
        }
    }
}
