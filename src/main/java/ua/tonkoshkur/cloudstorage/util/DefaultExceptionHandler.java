package ua.tonkoshkur.cloudstorage.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import ua.tonkoshkur.cloudstorage.file.FileAlreadyExistsException;
import ua.tonkoshkur.cloudstorage.file.InvalidFileNameException;
import ua.tonkoshkur.cloudstorage.folder.FolderAlreadyExistsException;
import ua.tonkoshkur.cloudstorage.folder.InvalidFolderNameException;

@ControllerAdvice
public class DefaultExceptionHandler {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @ExceptionHandler({
            FileAlreadyExistsException.class,
            FolderAlreadyExistsException.class,
            InvalidFileNameException.class,
            InvalidFolderNameException.class})
    public String handleCustomException(Exception e, HttpServletRequest request) {
        return UrlHelper.buildRefererRedirectUrlWithParam(request, "error", e.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceededException(HttpServletRequest request) {
        return UrlHelper.buildRefererRedirectUrlWithParam(request, "error",
                "File size must less than " + maxFileSize);
    }
}