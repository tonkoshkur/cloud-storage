package ua.tonkoshkur.cloudstorage.file;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;
import ua.tonkoshkur.cloudstorage.user.CustomUserDetails;
import ua.tonkoshkur.cloudstorage.util.PathHelper;
import ua.tonkoshkur.cloudstorage.util.RedirectHelper;

@RequestMapping("file")
@Controller
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping
    public RedirectView upload(MultipartFile file,
                               String folderPath,
                               HttpServletRequest request,
                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        fileService.upload(userDetails.user().getId(), file, folderPath);
        return RedirectHelper.buildRefererRedirectView(request);
    }

    @GetMapping
    public ResponseEntity<InputStreamResource> download(@RequestParam String path,
                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        InputStreamResource resource = fileService.download(userDetails.user().getId(), path);
        String fileName = PathHelper.extractName(path);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PatchMapping
    public RedirectView rename(String oldPath,
                               String newName,
                               HttpServletRequest request,
                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        fileService.rename(userDetails.user().getId(), oldPath, newName);
        return RedirectHelper.buildRefererRedirectView(request);
    }

    @DeleteMapping
    public RedirectView delete(String path,
                               HttpServletRequest request,
                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        fileService.delete(userDetails.user().getId(), path);
        return RedirectHelper.buildRefererRedirectView(request);
    }
}
