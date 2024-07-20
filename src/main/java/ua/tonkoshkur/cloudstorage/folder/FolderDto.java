package ua.tonkoshkur.cloudstorage.folder;

import javax.annotation.Nullable;

public record FolderDto(String name, @Nullable String parentFolderPath, String path) {
}
