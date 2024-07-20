package ua.tonkoshkur.cloudstorage.folder;

import io.minio.Result;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ua.tonkoshkur.cloudstorage.util.PathHelper;

import java.util.ArrayList;
import java.util.List;

import static ua.tonkoshkur.cloudstorage.util.PathHelper.PATH_SEPARATOR;

@Component
public class MinioResultItemsToFolderDtoMapper {

    @SneakyThrows
    public List<FolderDto> map(Iterable<Result<Item>> results, String userFolderPath) {
        List<FolderDto> folders = new ArrayList<>();
        for (Result<Item> result : results) {
            Item item = result.get();
            String fullPath = item.objectName();
            if (isFile(fullPath) || userFolderPath.equals(fullPath)) {
                continue;
            }
            FolderDto folder = mapItem(item, userFolderPath);
            folders.add(folder);
        }
        return folders;
    }

    private FolderDto mapItem(Item item, String userFolderPath) {
        String path = getShortPath(userFolderPath, item.objectName());
        return new FolderDto(
                PathHelper.extractName(path),
                PathHelper.extractParentFolder(path),
                path);
    }

    private boolean isFile(String path) {
        return !path.endsWith(PATH_SEPARATOR);
    }

    private String getShortPath(String userFolderPath, String fullPath) {
        return fullPath.substring(userFolderPath.length(), fullPath.length() - 1);
    }
}
