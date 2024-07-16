package ua.tonkoshkur.cloudstorage.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URLEncoder;
import java.nio.charset.Charset;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UrlHelper {

    private static final String PARAM_START = "?";
    private static final String PARAM_SEPARATOR = "&";
    private static final String PARAM_KEY_VALUE_SEPARATOR = "=";

    public static String buildRefererRedirectUrl(HttpServletRequest request) {
        String result = "redirect:";

        String referer = request.getHeader("referer");
        String origin = request.getHeader("origin");
        if (referer != null) {
            result += referer.replaceFirst(origin, "");
        }

        return result;
    }

    public static String buildRefererRedirectUrlWithParam(HttpServletRequest request, String name, String value) {
        String url = buildRefererRedirectUrl(request);
        return containsParam(url, name)
                ? replaceParam(url, name, value)
                : addParam(url, name, value);
    }

    private static boolean containsParam(String url, String name) {
        return url.contains(PARAM_START + name + PARAM_KEY_VALUE_SEPARATOR)
                || url.contains(PARAM_SEPARATOR + name + PARAM_KEY_VALUE_SEPARATOR);
    }

    private static String replaceParam(String url, String name, String newValue) {
        int index = url.indexOf(name + PARAM_KEY_VALUE_SEPARATOR);
        int endOfParamIndex = url.indexOf(PARAM_SEPARATOR, index + 1);
        if (endOfParamIndex < 0) {
            endOfParamIndex = url.length();
        }

        String oldParam = url.substring(index, endOfParamIndex);
        String newParam = buildEncodedParam(name, newValue);
        return url.replace(oldParam, newParam);
    }

    private static String addParam(String url, String name, String value) {
        String paramSeparator = url.contains(PARAM_START) ? PARAM_SEPARATOR : PARAM_START;
        return url + paramSeparator + buildEncodedParam(name, value);
    }

    private static String buildEncodedParam(String name, String value) {
        return name + PARAM_KEY_VALUE_SEPARATOR + URLEncoder.encode(value, Charset.defaultCharset());
    }
}
