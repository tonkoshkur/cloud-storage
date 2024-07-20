package ua.tonkoshkur.cloudstorage.file;

import io.minio.Result;
import io.minio.messages.Item;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.tonkoshkur.cloudstorage.minio.MinioNameValidator;
import ua.tonkoshkur.cloudstorage.minio.MinioService;
import ua.tonkoshkur.cloudstorage.util.PathHelper;

import javax.annotation.Nullable;
import java.util.List;

import static ua.tonkoshkur.cloudstorage.util.PathHelper.PATH_SEPARATOR;

@Service
@RequiredArgsConstructor
public class MinioFileService implements FileService {

    private final MinioService minioService;
    private final MinioResultItemsToFileDtoMapper resultItemsMapper;
    private final MinioNameValidator nameValidator;

    @Getter
    @Value("${minio.user-folder-format}")
    private String userFolderFormat;

    @SneakyThrows
    @Override
    public List<FileDto> findAllByQuery(long userId, String query) {
        String userFolderPath = getUserFolderPath(userId);
        Iterable<Result<Item>> results = minioService.findAll(userFolderPath, true);
        return resultItemsMapper.map(results, userFolderPath)
                .stream()
                .filter(file -> StringUtils.containsIgnoreCase(file.name(), query))
                .toList();
    }

    @SneakyThrows
    @Override
    public List<FileDto> findAllByFolderPath(long userId, @Nullable String folderPath) {
        String userFolderPath = getUserFolderPath(userId);
        String prefix = folderPath == null
                ? userFolderPath
                : userFolderPath + folderPath + PATH_SEPARATOR;

        Iterable<Result<Item>> results = minioService.findAll(prefix, false);

        return resultItemsMapper.map(results, userFolderPath);
    }

    @SneakyThrows
    @Override
    public InputStreamResource download(long userId, String path) {
        String fullPath = getFullPath(userId, path);
        return minioService.download(fullPath);
    }

    @SneakyThrows
    @Override
    public void upload(long userId, MultipartFile multipartFile, @Nullable String folderPath)
            throws InvalidFileNameException, FileAlreadyExistsException {
        String name = multipartFile.getOriginalFilename();
        FileDto file = new FileDto(name, folderPath);

        if (!nameValidator.isValid(name)) {
            throw new InvalidFileNameException(name);
        }
        if (exists(userId, file)) {
            throw new FileAlreadyExistsException(name);
        }

        String fullPath = getFullPath(userId, file.path());
        minioService.uploadFile(multipartFile, fullPath);
    }

    @SneakyThrows
    @Override
    public void rename(long userId, String oldPath, String newName)
            throws InvalidFileNameException, FileAlreadyExistsException {
        if (!nameValidator.isValid(newName)) {
            throw new InvalidFileNameException(newName);
        }

        String folderPath = PathHelper.extractParentFolder(oldPath);
        FileDto newFile = new FileDto(newName, folderPath);
        String newPath = newFile.path();

        if (newPath.equals(oldPath)) {
            return;
        }
        if (exists(userId, newFile)) {
            throw new FileAlreadyExistsException(newName);
        }

        copy(userId, oldPath, newPath);
        delete(userId, oldPath);
    }

    @SneakyThrows
    private boolean exists(long userId, FileDto file) {
        String fullPath = getFullPath(userId, file.path());
        return minioService.exists(fullPath);
    }

    @SneakyThrows
    private void copy(long userId, String fromPath, String toPath) {
        String fromFullPath = getFullPath(userId, fromPath);
        String toFullPath = getFullPath(userId, toPath);
        minioService.copy(fromFullPath, toFullPath);
    }

    @SneakyThrows
    @Override
    public void delete(long userId, String filePath) {
        String fullPath = getFullPath(userId, filePath);
        minioService.delete(fullPath);
    }

    private String getUserFolderPath(long userId) {
        return String.format(userFolderFormat, userId);
    }

    private String getFullPath(long userId, String path) {
        return getUserFolderPath(userId) + path;
    }
}
