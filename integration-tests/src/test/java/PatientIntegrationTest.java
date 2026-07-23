import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class PatientIntegrationTest {
    @BeforeAll
    static void setUp(){
        RestAssured.baseURI = "http://localhost:4004";
    }

    @Test
    public void shouldReturnPatientsWithValidToken () {
        String token = getToken();

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/patients")
                .then()
                .statusCode(200)
                .body("patients", notNullValue());
    }

    @Test
    public void shouldReturn429AfterLimitExceeded () throws InterruptedException {
        String token = getToken();
        int requestReturned429 = 0;
        for (int i=1; i<=10; i++) {
            Response response = given()
                    .header("Authorization", "Bearer " + token)
                    .get("api/patients");
            if (response.getStatusCode() == 429 ) {
                requestReturned429++;
            }
            System.out.println("Request " + i + ": Response status :" + response.getStatusCode());
            Thread.sleep(100);
        }

        Assertions.assertTrue(requestReturned429 >= 1, "Atleast one 429 response expected");


    }

    private static String getToken() {
        String loginPayload = """
          {
            "email": "testuser@test.com",
            "password": "password123"
          }
        """;

        String token = given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("token");
        return token;
    }
}