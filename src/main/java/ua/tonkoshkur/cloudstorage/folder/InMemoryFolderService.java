package ua.tonkoshkur.cloudstorage.folder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.tonkoshkur.cloudstorage.util.PathHelper;

import java.util.*;

import static ua.tonkoshkur.cloudstorage.util.PathHelper.PATH_SEPARATOR;

@Service
public class InMemoryFolderService implements FolderService {

    private final Map<String, FolderDto> foldersByFullPath = new HashMap<>();

    @Value("${validation.name-regex}")
    private String nameRegex;

    @Override
    public List<FolderDto> findAllByQuery(long userId, String query) {
        String userFolder = getUserFolderPath(userId);
        return foldersByFullPath.entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith(userFolder))
                .map(Map.Entry::getValue)
                .filter(folder -> folder.name().toLowerCase().contains(query.toLowerCase()))
                .sorted(Comparator.comparing(FolderDto::name))
                .toList();
    }

    @Override
    public List<FolderDto> findAllByParentPath(long userId, String parentFolderPath) {
        String userFolder = getUserFolderPath(userId);
        return foldersByFullPath.entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith(userFolder))
                .map(Map.Entry::getValue)
                .filter(folder -> Objects.equals(folder.parentFolderPath(), parentFolderPath))
                .sorted(Comparator.comparing(FolderDto::name))
                .toList();
    }

    @Override
    public void create(long userId, String name, String parentFolderPath)
            throws InvalidFolderNameException, FolderAlreadyExistsException {
        validateName(name);
        FolderDto folder = new FolderDto(name, parentFolderPath);
        throwIfExists(userId, folder);
        String fullPath = getFullPath(userId, folder.path());
        foldersByFullPath.put(fullPath, folder);
    }

    @Override
    public void rename(long userId, String oldPath, String newName)
            throws InvalidFolderNameException, FolderAlreadyExistsException {
        validateName(newName);
        String parentFolderPath = PathHelper.excludeParentFolder(oldPath);
        FolderDto folder = new FolderDto(newName, parentFolderPath);
        throwIfExists(userId, folder);
        String oldFullPath = getFullPath(userId, oldPath);
        foldersByFullPath.remove(oldFullPath);
        String newFullPath = getFullPath(userId, folder.path());
        foldersByFullPath.put(newFullPath, folder);
    }

    @Override
    public void delete(long userId, String path) {
        String fullPath = getFullPath(userId, path);
        foldersByFullPath.remove(fullPath);
    }

    private void throwIfExists(long userId, FolderDto folder) throws FolderAlreadyExistsException {
        String fullPath = getFullPath(userId, folder.path());
        FolderDto existedFolder = foldersByFullPath.get(fullPath);
        if (Objects.equals(existedFolder, folder)) {
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

    private String getUserFolderPath(long userId) {
        return "User" + userId;
    }
}
