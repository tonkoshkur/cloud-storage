package ua.tonkoshkur.cloudstorage.folder;

import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.tonkoshkur.cloudstorage.common.SynchronizedOnUser;
import ua.tonkoshkur.cloudstorage.minio.MinioNameValidator;
import ua.tonkoshkur.cloudstorage.minio.MinioService;
import ua.tonkoshkur.cloudstorage.util.PathHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static ua.tonkoshkur.cloudstorage.util.PathHelper.PATH_SEPARATOR;

@Service
@RequiredArgsConstructor
public class MinioFolderService implements FolderService {

    private final MinioService minioService;
    private final MinioResultItemsToFolderDtoMapper resultItemsMapper;
    private final MinioNameValidator nameValidator;

    @Value("${minio.user-folder-format}")
    private String userFolderFormat;

    @SneakyThrows
    @Override
    public List<FolderDto> findAllByQuery(long userId, String query) {
        String userFolderPath = getUserFolderPath(userId);
        Iterable<Result<Item>> results = minioService.findAll(userFolderPath, true);
        return resultItemsMapper.map(results, userFolderPath)
                .stream()
                .filter(folder -> StringUtils.containsIgnoreCase(folder.name(), query))
                .toList();
    }

    @SneakyThrows
    @Override
    public List<FolderDto> findAllByParentPath(long userId, @Nullable String parentFolderPath) {
        String userFolderPath = getUserFolderPath(userId);
        String prefix = parentFolderPath == null
                ? userFolderPath
                : getFullPath(userId, parentFolderPath);

        Iterable<Result<Item>> results = minioService.findAll(prefix, false);

        return resultItemsMapper.map(results, userFolderPath)
                .stream()
                .filter(folder -> !folder.path().equals(parentFolderPath))
                .toList();
    }

    @SynchronizedOnUser
    @SneakyThrows
    @Override
    public void create(long userId, String name, @Nullable String parentFolderPath)
            throws InvalidFolderNameException, FolderAlreadyExistsException {
        String path = PathHelper.buildPath(name, parentFolderPath);

        if (!nameValidator.isValid(name)) {
            throw new InvalidFolderNameException(name);
        }
        if (exists(userId, path)) {
            throw new FolderAlreadyExistsException(name);
        }

        String fullPath = getFullPath(userId, path);
        minioService.createFolder(fullPath);
    }

    @SynchronizedOnUser
    @SneakyThrows
    @Override
    public void rename(long userId, String oldPath, String newName)
            throws InvalidFolderNameException, FolderAlreadyExistsException {
        if (!nameValidator.isValid(newName)) {
            throw new InvalidFolderNameException(newName);
        }

        String parentFolderPath = PathHelper.extractParentFolder(oldPath);
        String newPath = PathHelper.buildPath(newName, parentFolderPath);

        if (newPath.equals(oldPath)) {
            return;
        }
        if (exists(userId, newPath)) {
            throw new FolderAlreadyExistsException(newName);
        }

        copy(userId, oldPath, newPath);
        delete(userId, oldPath);
    }

    @SneakyThrows
    private boolean exists(long userId, String path) {
        String fullPath = getFullPath(userId, path);
        return minioService.exists(fullPath);
    }

    @SynchronizedOnUser
    @SneakyThrows
    private void copy(long userId, String fromPath, String toPath) {
        String fromFullPath = getFullPath(userId, fromPath);
        String toFullPath = getFullPath(userId, toPath);
        Iterable<Result<Item>> results = minioService.findAll(fromFullPath, true);
        for (Result<Item> result : results) {
            String from = result.get().objectName();
            String to = from.replaceFirst(fromFullPath, toFullPath);
            minioService.copy(from, to);
        }
    }

    @SynchronizedOnUser
    @SneakyThrows
    @Override
    public void delete(long userId, String folderPath) {
        String fullPath = getFullPath(userId, folderPath);
        Iterable<Result<Item>> results = minioService.findAll(fullPath, true);
        List<String> paths = new ArrayList<>();
        for (Result<Item> resultItem : results) {
            paths.add(resultItem.get().objectName());
        }
        minioService.deleteAll(paths);
    }

    private String getFullPath(long userId, String path) {
        return getUserFolderPath(userId) + path + PATH_SEPARATOR;
    }

    private String getUserFolderPath(long userId) {
        return String.format(userFolderFormat, userId);
    }
}
