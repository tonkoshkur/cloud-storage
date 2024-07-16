package ua.tonkoshkur.cloudstorage.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ua.tonkoshkur.cloudstorage.file.FileAlreadyExistsException;
import ua.tonkoshkur.cloudstorage.file.InvalidFileNameException;
import ua.tonkoshkur.cloudstorage.folder.FolderAlreadyExistsException;
import ua.tonkoshkur.cloudstorage.folder.InvalidFolderNameException;

@ControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler({
            FileAlreadyExistsException.class,
            FolderAlreadyExistsException.class,
            InvalidFileNameException.class,
            InvalidFolderNameException.class})
    public String handleCustomException(Exception e, HttpServletRequest request) {
        return UrlHelper.buildRefererRedirectUrlWithParam(request, "error", e.getMessage());
    }
}
