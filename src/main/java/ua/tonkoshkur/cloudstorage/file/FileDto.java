package ua.tonkoshkur.cloudstorage.file;

import javax.annotation.Nullable;

public record FileDto(String name, @Nullable String folderPath, String path) {
}
