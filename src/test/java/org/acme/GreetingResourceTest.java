package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

import org.hamcrest.core.IsAnything;

@QuarkusTest
public class GreetingResourceTest {

    @Test
    public void testPingEndpoint() {
        given()
          .when().get("/sistema/ping")
          .then()
             .statusCode(200)
             .body(IsAnything.anything());
    }

}