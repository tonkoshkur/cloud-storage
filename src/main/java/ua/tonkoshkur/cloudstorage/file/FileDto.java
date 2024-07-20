package ua.tonkoshkur.cloudstorage.file;

import javax.annotation.Nullable;
import java.time.LocalDateTime;

public record FileDto(String name, @Nullable String folderPath, String path, String size, LocalDateTime modifiedAt) {
}
