package ua.tonkoshkur.cloudstorage.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.view.RedirectView;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedirectHelper {

    public static RedirectView buildRefererRedirectView(HttpServletRequest request) {
        return new RedirectView(
                request.getHeader("Referer"));
    }
}
