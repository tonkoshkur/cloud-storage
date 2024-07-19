package ua.tonkoshkur.cloudstorage.file;

public class InvalidFileNameException extends RuntimeException {
    public InvalidFileNameException(String name) {
        super(String.format("Invalid file name: '%s'. Special characters are not allowed", name));
    }
}
