package ua.tonkoshkur.cloudstorage.file;

import javax.annotation.Nullable;

import static ua.tonkoshkur.cloudstorage.util.PathHelper.PATH_SEPARATOR;

public record FileDto(String name, @Nullable String folderPath) {
    public String path() {
        return folderPath == null
                ? name
                : folderPath + PATH_SEPARATOR + name;
    }
}
