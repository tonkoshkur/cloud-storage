package ua.tonkoshkur.cloudstorage.minio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

@Component
public class MinioNameValidator {

    @Value("${minio.invalid-character-regex}")
    private String invalidCharacterRegex;

    public boolean isValid(@Nullable String name) {
        return name != null
                && !name.isBlank()
                && !name.matches(invalidCharacterRegex);
    }
}
