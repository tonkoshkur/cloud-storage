package ua.tonkoshkur.cloudstorage.folder;

import static ua.tonkoshkur.cloudstorage.util.PathHelper.PATH_SEPARATOR;

public record FolderDto(String name, String parentFolderPath) {
    public String path() {
        return parentFolderPath == null
                ? name
                : parentFolderPath + PATH_SEPARATOR + name;
    }
}
