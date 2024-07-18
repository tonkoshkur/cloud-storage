package ua.tonkoshkur.cloudstorage;

import io.restassured.RestAssured;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest {

    @Getter
    protected static String[] invalidMinioCharacters = {"^", "*", "|", "\\", "/", "&", "\"", ";"};

    @LocalServerPort
    protected int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }
}
