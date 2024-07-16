package ua.tonkoshkur.cloudstorage.file;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    List<FileDto> findAllByFolderPath(long userId, String folderPath);

    InputStreamResource download(long userId, String path);

    void upload(long userId, MultipartFile multipartFile, String folderPath)
            throws InvalidFileNameException, FileAlreadyExistsException;

    void rename(long userId, String oldPath, String newName)
            throws InvalidFileNameException, FileAlreadyExistsException;

    void delete(long userId, String filePath);
}
