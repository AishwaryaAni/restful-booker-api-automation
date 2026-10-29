package com.qa.api.client;

import com.qa.api.config.ConfigReader;
import com.qa.api.models.AuthRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/** POST /auth — issues the token required by PUT, PATCH and DELETE. */
public class AuthClient {

    private static volatile String cachedToken;

    @Step("POST /auth")
    public Response createToken(AuthRequest request) {
        return given().spec(SpecFactory.baseSpec())
                .body(request)
                .when().post("/auth");
    }

    /** Token for the configured admin user, fetched once per run and reused (like a logged-in session). */
    public static synchronized String adminToken() {
        if (cachedToken == null) {
            AuthRequest admin = new AuthRequest(ConfigReader.get("auth.username"), ConfigReader.get("auth.password"));
            cachedToken = new AuthClient().createToken(admin)
                    .then().statusCode(200)
                    .extract().path("token");
            if (cachedToken == null || cachedToken.isBlank()) {
                throw new IllegalStateException("Auth did not return a token — check credentials in config.properties");
            }
        }
        return cachedToken;
    }
}
