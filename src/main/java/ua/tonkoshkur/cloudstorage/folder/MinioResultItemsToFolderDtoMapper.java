package ua.tonkoshkur.cloudstorage.folder;

import io.minio.Result;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ua.tonkoshkur.cloudstorage.util.PathHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static ua.tonkoshkur.cloudstorage.util.PathHelper.PATH_SEPARATOR;

@Component
public class MinioResultItemsToFolderDtoMapper {

    @SneakyThrows
    public List<FolderDto> map(Iterable<Result<Item>> results, String userFolderPath, @Nullable String query) {
        List<FolderDto> folders = new ArrayList<>();
        for (Result<Item> result : results) {
            Item item = result.get();
            if (shouldSkipResultItem(item, userFolderPath, query)) {
                continue;
            }
            String path = getShortPath(userFolderPath, item.objectName());
            folders.add(new FolderDto(
                    PathHelper.extractName(path),
                    PathHelper.extractParentFolder(path)));
        }
        return folders;
    }

    private boolean shouldSkipResultItem(Item item, String userFolderPath, @Nullable String query) {
        if (query == null && !item.isDir()) {
            return true;
        }
        String fullPath = item.objectName();
        if (query != null && !getShortPath(userFolderPath, fullPath).contains(query)) {
            return true;
        }
        return !fullPath.endsWith(PATH_SEPARATOR) || userFolderPath.equals(fullPath);
    }

    private String getShortPath(String userFolderPath, String fullPath) {
        return fullPath.substring(userFolderPath.length(), fullPath.length() - 1);
    }
}
