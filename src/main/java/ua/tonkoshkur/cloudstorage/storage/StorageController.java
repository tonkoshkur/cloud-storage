package ua.tonkoshkur.cloudstorage.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.tonkoshkur.cloudstorage.breadcrumb.BreadcrumbDto;
import ua.tonkoshkur.cloudstorage.breadcrumb.BreadcrumbService;
import ua.tonkoshkur.cloudstorage.user.CustomUserDetails;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StorageController {

    private static final String STORAGE_PAGE = "storage";
    private final StorageService storageService;
    private final BreadcrumbService breadcrumbService;

    @GetMapping
    public String storagePage(@RequestParam(required = false) String path,
                              Model model,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        model.addAttribute("path", path);

        StorageContentDto content = storageService.findContentByPath(userDetails.user().getId(), path);
        model.addAttribute("content", content);

        if (path != null) {
            addBreadcrumbs(path, model);
        }

        return STORAGE_PAGE;
    }

    private void addBreadcrumbs(String path, Model model) {
        List<BreadcrumbDto> breadcrumbs = breadcrumbService.createBreadcrumbs(path);
        model.addAttribute("breadcrumbs", breadcrumbs);

        int breadcrumbsCount = breadcrumbs.size();
        if (breadcrumbsCount > 1) {
            model.addAttribute("parentFolderPath", breadcrumbs.get(breadcrumbsCount - 2).path());
        }
    }

    @GetMapping("search")
    public String search(@RequestParam String query,
                         Model model,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        model.addAttribute("query", query);

        StorageContentDto content = storageService.findContentByName(userDetails.user().getId(), query);
        model.addAttribute("content", content);

        return STORAGE_PAGE;
    }
}

