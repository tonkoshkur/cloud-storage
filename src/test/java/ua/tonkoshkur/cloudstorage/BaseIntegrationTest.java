package ua.tonkoshkur.cloudstorage;

import io.restassured.RestAssured;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest {

    @Getter
    protected static String[] invalidMinioCharacters;

    @LocalServerPort
    protected int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Value("${minio.invalid-character-regex}")
    private void setInvalidMinioCharacters(String regex) {
        String content = regex.substring(1, regex.length() - 1);
        invalidMinioCharacters = content.chars()
                .mapToObj(c -> String.valueOf((char) c))
                .toArray(String[]::new);
    }
}
