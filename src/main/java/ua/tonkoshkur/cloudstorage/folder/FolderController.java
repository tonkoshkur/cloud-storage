package ua.tonkoshkur.cloudstorage.folder;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;
import ua.tonkoshkur.cloudstorage.user.CustomUserDetails;
import ua.tonkoshkur.cloudstorage.util.RedirectHelper;

@RequestMapping("folder")
@Controller
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    @PostMapping
    public RedirectView create(String name,
                               String parentFolderPath,
                               HttpServletRequest request,
                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        folderService.create(userDetails.user().getId(), name, parentFolderPath);
        return RedirectHelper.buildRefererRedirectView(request);
    }

    @PatchMapping
    public RedirectView rename(String oldPath,
                               String newName,
                               HttpServletRequest request,
                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        folderService.rename(userDetails.user().getId(), oldPath, newName);
        return RedirectHelper.buildRefererRedirectView(request);
    }

    @DeleteMapping
    public RedirectView delete(String path,
                               HttpServletRequest request,
                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        folderService.delete(userDetails.user().getId(), path);
        return RedirectHelper.buildRefererRedirectView(request);
    }
}
