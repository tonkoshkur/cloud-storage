package ua.tonkoshkur.cloudstorage.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PathHelper {

    public static final String PATH_SEPARATOR = "/";

    public static String extractName(String path) {
        return path.contains(PATH_SEPARATOR)
                ? path.substring(path.lastIndexOf(PATH_SEPARATOR) + 1)
                : path;
    }

    @Nullable
    public static String extractParentFolder(@Nullable String path) {
        if (path == null || !path.contains(PATH_SEPARATOR)) {
            return null;
        }
        String parentFolder = path.substring(0, path.lastIndexOf(PATH_SEPARATOR));
        return parentFolder.isEmpty()
                ? null
                : parentFolder;
    }

    public static String buildPath(String name, @Nullable String parentFolderPath) {
        return parentFolderPath == null
                ? name
                : parentFolderPath + PATH_SEPARATOR + name;
    }
}
