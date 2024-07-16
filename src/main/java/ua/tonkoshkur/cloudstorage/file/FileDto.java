package ua.tonkoshkur.cloudstorage.file;

import static ua.tonkoshkur.cloudstorage.util.PathHelper.PATH_SEPARATOR;

public record FileDto(String name, String folderPath) {
    public String path() {
        return folderPath == null
                ? name
                : folderPath + PATH_SEPARATOR + name;
    }
}
