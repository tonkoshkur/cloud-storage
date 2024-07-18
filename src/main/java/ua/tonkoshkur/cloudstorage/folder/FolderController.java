package ua.tonkoshkur.cloudstorage.folder;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.tonkoshkur.cloudstorage.user.CustomUserDetails;
import ua.tonkoshkur.cloudstorage.util.UrlHelper;

@RequestMapping("folder")
@Controller
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    @PostMapping
    public String create(String name,
                         String parentFolderPath,
                         HttpServletRequest request,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        folderService.create(userDetails.user().getId(), name, parentFolderPath);
        return UrlHelper.buildRefererRedirectUrl(request);
    }

    @PutMapping
    public String rename(String oldPath,
                         String newName,
                         HttpServletRequest request,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        folderService.rename(userDetails.user().getId(), oldPath, newName);
        return UrlHelper.buildRefererRedirectUrl(request);
    }

    @DeleteMapping
    public String delete(String path,
                         HttpServletRequest request,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        folderService.delete(userDetails.user().getId(), path);
        return UrlHelper.buildRefererRedirectUrl(request);
    }
}
