package ua.tonkoshkur.cloudstorage.file;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.tonkoshkur.cloudstorage.util.PathHelper;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

@Service
public class InMemoryFileService implements FileService {

    private final Map<String, FileDto> filesByFullPath = new HashMap<>();

    @Value("${validation.name-regex}")
    private String nameRegex;

    @Override
    public List<FileDto> findAllByQuery(long userId, String query) {
        String userFolder = getUserFolderPath(userId);
        return filesByFullPath.entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith(userFolder))
                .map(Map.Entry::getValue)
                .filter(folder -> folder.name().toLowerCase().contains(query.toLowerCase()))
                .sorted(Comparator.comparing(FileDto::name))
                .toList();
    }

    @Override
    public List<FileDto> findAllByFolderPath(long userId, String folderPath) {
        String userFolder = getUserFolderPath(userId);
        return filesByFullPath.entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith(userFolder))
                .map(Map.Entry::getValue)
                .filter(file -> Objects.equals(file.folderPath(), folderPath))
                .sorted(Comparator.comparing(FileDto::name))
                .toList();
    }

    @SneakyThrows
    @Override
    public InputStreamResource download(long userId, String path) {
        File file = new File("Dockerfile");
        return new InputStreamResource(new FileInputStream(file));
    }

    @Override
    public void upload(long userId, MultipartFile multipartFile, String folderPath)
            throws InvalidFileNameException, FileAlreadyExistsException {
        FileDto file = new FileDto(multipartFile.getOriginalFilename(), folderPath);
        validateName(file.name());
        throwIfExists(userId, file);
        String fullPath = getFullPath(userId, file.path());
        filesByFullPath.put(fullPath, file);
    }

    @Override
    public void rename(long userId, String oldPath, String newName)
            throws InvalidFileNameException, FileAlreadyExistsException {
        validateName(newName);
        String folderPath = PathHelper.extractParentFolder(oldPath);
        FileDto file = new FileDto(newName, folderPath);
        throwIfExists(userId, file);
        String oldFullPath = getFullPath(userId, oldPath);
        filesByFullPath.remove(oldFullPath);
        String newFullPath = getFullPath(userId, file.path());
        filesByFullPath.put(newFullPath, file);
    }

    @Override
    public void delete(long userId, String filePath) {
        filesByFullPath.remove(filePath);
    }

    private void throwIfExists(long userId, FileDto file) throws FileAlreadyExistsException {
        String fullPath = getFullPath(userId, file.path());
        FileDto existedFile = filesByFullPath.get(fullPath);
        if (Objects.equals(existedFile, file)) {
            throw new FileAlreadyExistsException(file.name());
        }
    }

    private void validateName(String name) throws InvalidFileNameException {
        if (name == null || !name.matches(nameRegex)) {
            throw new InvalidFileNameException(name);
        }
    }

    private String getUserFolderPath(long userId) {
        return "User" + userId;
    }

    private String getFullPath(long userId, String path) {
        return getUserFolderPath(userId) + path;
    }
}
