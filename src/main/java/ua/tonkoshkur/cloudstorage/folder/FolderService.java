package ua.tonkoshkur.cloudstorage.folder;

import java.util.List;

public interface FolderService {
    List<FolderDto> findAllByParentPath(long userId, String parentFolderPath);

    void create(long userId, String name, String parentFolderPath)
            throws InvalidFolderNameException, FolderAlreadyExistsException;

    void rename(long userId, String oldPath, String newName)
            throws InvalidFolderNameException, FolderAlreadyExistsException;

    void delete(long userId, String folderPath);
}
