package ua.tonkoshkur.cloudstorage.file;

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
public class MinioResultItemsToFileDtoMapper {

    @SneakyThrows
    public List<FileDto> map(Iterable<Result<Item>> results, String userFolderPath, @Nullable String query) {
        List<FileDto> folders = new ArrayList<>();
        for (Result<Item> result : results) {
            String path = getShortPath(userFolderPath, result.get().objectName());
            if (shouldSkipResultItem(path, query)) {
                continue;
            }
            folders.add(new FileDto(
                    PathHelper.extractName(path),
                    PathHelper.extractParentFolder(path)));
        }
        return folders;
    }

    private String getShortPath(String userFolderPath, String fullPath) {
        return fullPath.replaceFirst(userFolderPath, "");
    }

    private boolean shouldSkipResultItem(String path, @Nullable String query) {
        if (query != null && !path.contains(query)) {
            return true;
        }
        return path.endsWith(PATH_SEPARATOR);
    }
}
