package ua.tonkoshkur.cloudstorage.auth.signout;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.tonkoshkur.cloudstorage.BaseIntegrationTest;
import ua.tonkoshkur.cloudstorage.user.UserRepository;
import ua.tonkoshkur.cloudstorage.user.UserService;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;

class SignOutIntegrationTest extends BaseIntegrationTest {

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
    void getRequest_withAuthenticatedUser_redirectsToSignInPage() {
        String username = "user";
        String password = "pass";
        userService.save(username, password);
        given()
                .formParam("username", username)
                .formParam("password", password)
                .when()
                .post("/auth/signin")
                .then()
                .statusCode(302)
                .header("Location", endsWith("/"));

        given()
                .redirects().follow(false)
                .get("/auth/signout")
                .then()
                .statusCode(302)
                .header("Location", endsWith("/auth/signin"));
    }
}
