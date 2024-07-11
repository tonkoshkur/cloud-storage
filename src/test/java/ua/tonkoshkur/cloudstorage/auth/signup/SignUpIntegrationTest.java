package ua.tonkoshkur.cloudstorage.auth.signup;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.tonkoshkur.cloudstorage.BaseIntegrationTest;
import ua.tonkoshkur.cloudstorage.user.User;
import ua.tonkoshkur.cloudstorage.user.UserRepository;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SignUpIntegrationTest extends BaseIntegrationTest {

    private static final String PATH = "/auth/signup";
    private static final String USERNAME_PARAM = "username";
    private static final String PASSWORD_PARAM = "password";
    private static final String CONFIRM_PASSWORD_PARAM = "confirmPassword";

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        super.setUp();
        userRepository.deleteAll();
    }

    @Test
    void postRequest_withValidFormParamsAndNotExistedUser_returnsSignInPage() {
        String password = "pass";

        given()
                .formParam(USERNAME_PARAM, "user")
                .formParam(PASSWORD_PARAM, password)
                .formParam(CONFIRM_PASSWORD_PARAM, password)
                .when()
                .post(PATH)
                .then()
                .statusCode(302)
                .header("Location", endsWith("/auth/signin"));
    }

    @Test
    void postRequest_withValidFormParamsAndExistedUser_returnsSignUpPageWithErrorMessage() {
        String username = "user";
        String password = "pass";
        userRepository.save(new User(username, password));

        Response response = given()
                .formParam(USERNAME_PARAM, username)
                .formParam(PASSWORD_PARAM, password)
                .formParam(CONFIRM_PASSWORD_PARAM, password)
                .when()
                .post(PATH)
                .then()
                .statusCode(200)
                .extract().response();

        assertEquals("User already exists", getErrorMessage(response));
    }

    @Test
    void postRequest_withInvalidUsername_returnsPageWithErrorMessage() {
        String password = "pass";

        Response response = given()
                .formParam(USERNAME_PARAM, "u")
                .formParam(PASSWORD_PARAM, password)
                .formParam(CONFIRM_PASSWORD_PARAM, password)
                .when()
                .post(PATH)
                .then()
                .statusCode(200)
                .extract().response();

        assertEquals("Username must be 3+ characters", getErrorMessage(response));
    }

    @Test
    void postRequest_withInvalidPassword_returnsPageWithErrorMessage() {
        String invalidPassword = "p";

        Response response = given()
                .formParam(USERNAME_PARAM, "user")
                .formParam(PASSWORD_PARAM, invalidPassword)
                .formParam(CONFIRM_PASSWORD_PARAM, invalidPassword)
                .when()
                .post(PATH)
                .then()
                .statusCode(200)
                .extract().response();

        assertEquals("Password must be 3+ characters", getErrorMessage(response));
    }

    @Test
    void postRequest_withDifferentPasswords_returnsPageWithErrorMessage() {
        Response response = given()
                .formParam(USERNAME_PARAM, "user")
                .formParam(PASSWORD_PARAM, "pass")
                .formParam(CONFIRM_PASSWORD_PARAM, "pas")
                .when()
                .post(PATH)
                .then()
                .statusCode(200)
                .extract().response();

        assertEquals("Passwords do not match", getErrorMessage(response));
    }

    private String getErrorMessage(Response response) {
        return response.htmlPath().getString("**.find { it.@class == 'text-danger' }");
    }
}
