package ua.tonkoshkur.cloudstorage.util;

import io.minio.errors.ErrorResponseException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import ua.tonkoshkur.cloudstorage.file.FileAlreadyExistsException;
import ua.tonkoshkur.cloudstorage.file.InvalidFileNameException;
import ua.tonkoshkur.cloudstorage.folder.FolderAlreadyExistsException;
import ua.tonkoshkur.cloudstorage.folder.InvalidFolderNameException;

@ControllerAdvice
public class DefaultExceptionHandler {

    private static final String ERROR_PARAM = "error";

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @ExceptionHandler({
            FileAlreadyExistsException.class,
            FolderAlreadyExistsException.class,
            InvalidFileNameException.class,
            InvalidFolderNameException.class})
    public RedirectView handleCustomException(Exception e,
                                              HttpServletRequest request,
                                              RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(ERROR_PARAM, e.getMessage());
        return RedirectHelper.buildRefererRedirectView(request);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public RedirectView handleMaxUploadSizeExceededException(HttpServletRequest request,
                                                             RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(ERROR_PARAM, "File size must less than " + maxFileSize);
        return RedirectHelper.buildRefererRedirectView(request);
    }

    @ExceptionHandler(ErrorResponseException.class)
    public RedirectView handleMinioErrorResponseException(HttpServletRequest request,
                                                          RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(ERROR_PARAM, "Resource not found");
        return RedirectHelper.buildRefererRedirectView(request);
    }
}