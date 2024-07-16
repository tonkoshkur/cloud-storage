package ua.tonkoshkur.cloudstorage.folder;

public class FolderAlreadyExistsException extends RuntimeException {
    public FolderAlreadyExistsException(String folderName) {
        super(String.format("Folder with name '%s' already exists in this folder", folderName));
    }
}
