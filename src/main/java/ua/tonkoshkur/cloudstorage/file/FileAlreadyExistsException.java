package ua.tonkoshkur.cloudstorage.file;

public class FileAlreadyExistsException extends RuntimeException {
    public FileAlreadyExistsException(String fileName) {
        super(String.format("File with name '%s' already exists in this folder", fileName));
    }
}
