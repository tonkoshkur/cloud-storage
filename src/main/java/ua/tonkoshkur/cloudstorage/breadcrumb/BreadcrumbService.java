package ua.tonkoshkur.cloudstorage.breadcrumb;

import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

import static ua.tonkoshkur.cloudstorage.util.PathHelper.PATH_SEPARATOR;

@Service
public class BreadcrumbService {

    public List<BreadcrumbDto> createBreadcrumbs(String path) {
        List<BreadcrumbDto> breadcrumbs = new LinkedList<>();
        StringBuilder pathBuilder = new StringBuilder();

        for (String part : path.split(PATH_SEPARATOR)) {
            if (!pathBuilder.isEmpty()) {
                pathBuilder.append(PATH_SEPARATOR);
            }
            pathBuilder.append(part);
            String breadcrumbPath = pathBuilder.toString();
            breadcrumbs.add(new BreadcrumbDto(part, breadcrumbPath, breadcrumbPath.equals(path)));
        }

        return breadcrumbs;
    }
}
