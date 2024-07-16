package ua.tonkoshkur.cloudstorage.storage;

import org.junit.jupiter.api.Test;
import ua.tonkoshkur.cloudstorage.BaseIntegrationTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;

class StorageIntegrationTest extends BaseIntegrationTest {

    @Test
    void getRequest_withUnauthenticatedUser_redirectsToSignInPage() {
        given()
                .redirects().follow(false)
                .when()
                .get("/")
                .then()
                .statusCode(302)
                .header("Location", endsWith("/auth/signin"));
    }
}
