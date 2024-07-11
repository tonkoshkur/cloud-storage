package ua.tonkoshkur.cloudstorage.auth.signin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.tonkoshkur.cloudstorage.BaseIntegrationTest;
import ua.tonkoshkur.cloudstorage.user.UserRepository;
import ua.tonkoshkur.cloudstorage.user.UserService;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;

class SignInIntegrationTest extends BaseIntegrationTest {

    private static final String PATH = "/auth/signin";
    private static final String USERNAME_PARAM = "username";
    private static final String PASSWORD_PARAM = "password";

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        super.setUp();
        userRepository.deleteAll();
    }

    @Test
    void postRequest_withExistedUser_redirectsToHomePage() {
        String username = "user";
        String password = "pass";
        userService.save(username, password);

        given()
                .formParam(USERNAME_PARAM, username)
                .formParam(PASSWORD_PARAM, password)
                .when()
                .post(PATH)
                .then()
                .statusCode(302)
                .header("Location", endsWith("/"));
    }

    @Test
    void postRequest_withNotExistedUser_returnsPageWithErrorParam() {
        given()
                .formParam(USERNAME_PARAM, "user")
                .formParam(PASSWORD_PARAM, "pass")
                .when()
                .post(PATH)
                .then()
                .statusCode(302)
                .header("Location", endsWith(PATH + "?error"));
    }
}
