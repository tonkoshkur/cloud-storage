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
    public List<FileDto> map(Iterable<Result<Item>> results, String userFolderPath) {
        List<FileDto> files = new ArrayList<>();
        for (Result<Item> result : results) {
            Item item = result.get();
            if (isFolder(item)) {
                continue;
            }
            FileDto file = map(item, userFolderPath);
            files.add(file);
        }
        return files;
    }

    private FileDto map(Item item, String userFolderPath) {
        String path = getShortPath(userFolderPath, item.objectName());
        return new FileDto(
                PathHelper.extractName(path),
                PathHelper.extractParentFolder(path));
    }

    private String getShortPath(String userFolderPath, String fullPath) {
        return fullPath.replaceFirst(userFolderPath, "");
    }

    private boolean isFolder(Item item) {
        return item.objectName().endsWith(PATH_SEPARATOR);
    }
}
