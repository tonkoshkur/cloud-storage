package ua.tonkoshkur.cloudstorage.folder;

public class InvalidFolderNameException extends RuntimeException {
    public InvalidFolderNameException(String name) {
        super(String.format("Invalid folder name: '%s'. " +
                "Folder names can only contain letters, numbers, dots, hyphens and underscores", name));
    }
}
