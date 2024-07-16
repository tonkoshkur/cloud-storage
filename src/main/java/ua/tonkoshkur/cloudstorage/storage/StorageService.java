package ua.tonkoshkur.cloudstorage.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.tonkoshkur.cloudstorage.file.FileService;
import ua.tonkoshkur.cloudstorage.folder.FolderService;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final FolderService folderService;
    private final FileService fileService;

    public StorageContentDto findContentByPath(long userId, String path) {
        return new StorageContentDto(
                folderService.findAllByParentPath(userId, path),
                fileService.findAllByFolderPath(userId, path));
    }
}
