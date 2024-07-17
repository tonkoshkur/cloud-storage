package ua.tonkoshkur.cloudstorage.folder;

import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ua.tonkoshkur.cloudstorage.minio.MinioService;
import ua.tonkoshkur.cloudstorage.util.PathHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static ua.tonkoshkur.cloudstorage.util.PathHelper.PATH_SEPARATOR;

@Primary
@Service
@RequiredArgsConstructor
public class MinioFolderService implements FolderService {

    private final MinioService minioService;

    @Value("${validation.name-regex}")
    private String nameRegex;

    @Value("${minio.user-folder-format}")
    private String userFolderFormat;

    @SneakyThrows
    @Override
    public List<FolderDto> findAllByQuery(long userId, String query) {
        String userFolderPath = getUserFolderPath(userId);
        Iterable<Result<Item>> results = minioService.findAll(userFolderPath, true);
        return mapResultItemsToFolders(results, userId, query);
    }

    @SneakyThrows
    @Override
    public List<FolderDto> findAllByParentPath(long userId, String parentFolderPath) {
        String userFolderPath = getUserFolderPath(userId);
        String prefix = parentFolderPath == null ? userFolderPath : getFullPath(userId, parentFolderPath);
        Iterable<Result<Item>> results = minioService.findAll(prefix, false);
        return mapResultItemsToFolders(results, userId, null);
    }

    @SneakyThrows
    private List<FolderDto> mapResultItemsToFolders(Iterable<Result<Item>> results, long userId, @Nullable String query) {
        List<FolderDto> folders = new ArrayList<>();
        for (Result<Item> result : results) {
            Item item = result.get();
            if (skipResultItem(item, userId, query)) {
                continue;
            }
            String path = getShortPath(userId, item.objectName());
            folders.add(new FolderDto(
                    PathHelper.extractName(path),
                    PathHelper.extractParentFolder(path)));
        }
        return folders;
    }

    private boolean skipResultItem(Item item, long userId, @Nullable String query) {
        if (query == null && !item.isDir()) {
            return true;
        }
        String fullPath = item.objectName();
        if (query != null && !getShortPath(userId, fullPath).contains(query)) {
            return true;
        }
        return !fullPath.endsWith(PATH_SEPARATOR) || getUserFolderPath(userId).equals(fullPath);
    }

    @SneakyThrows
    @Override
    public void create(long userId, String name, String parentFolderPath)
            throws InvalidFolderNameException, FolderAlreadyExistsException {
        validateName(name);
        FolderDto folder = new FolderDto(name, parentFolderPath);
        throwIfExists(userId, folder);
        String fullPath = getFullPath(userId, folder.path());
        minioService.createFolder(fullPath);
    }

    @SneakyThrows
    @Override
    public void rename(long userId, String oldPath, String newName)
            throws InvalidFolderNameException, FolderAlreadyExistsException {
        validateName(newName);
        String parentFolderPath = PathHelper.extractParentFolder(oldPath);
        FolderDto newFolder = new FolderDto(newName, parentFolderPath);
        String newPath = newFolder.path();
        if (newPath.equals(oldPath)) {
            return;
        }
        throwIfExists(userId, newFolder);
        copy(userId, oldPath, newPath);
        delete(userId, oldPath);
    }

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

    @SneakyThrows
    @Override
    public void delete(long userId, String folderPath) {
        String fullPath = getFullPath(userId, folderPath);
        Iterable<Result<Item>> results = minioService.findAll(fullPath, true);
        for (Result<Item> resultItem : results) {
            minioService.delete(resultItem.get().objectName());
        }
    }

    @SneakyThrows
    private void throwIfExists(long userId, FolderDto folder) {
        String fullPath = getFullPath(userId, folder.path());
        if (minioService.exists(fullPath)) {
            throw new FolderAlreadyExistsException(folder.name());
        }
    }

    private void validateName(String name) throws InvalidFolderNameException {
        if (!name.matches(nameRegex)) {
            throw new InvalidFolderNameException(name);
        }
    }

    private String getFullPath(long userId, String path) {
        return getUserFolderPath(userId) + path + PATH_SEPARATOR;
    }

    private String getShortPath(long userId, String fullPath) {
        String userFolderPath = getUserFolderPath(userId);
        return fullPath.substring(userFolderPath.length(), fullPath.length() - 1);
    }

    private String getUserFolderPath(long userId) {
        return String.format(userFolderFormat, userId);
    }
}
