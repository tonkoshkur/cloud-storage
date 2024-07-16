package ua.tonkoshkur.cloudstorage.file;

public class InvalidFileNameException extends RuntimeException {
    public InvalidFileNameException(String name) {
        super(String.format("Invalid file name: '%s'. " +
                "File names can only contain letters, numbers, dots, hyphens and underscores", name));
    }
}
