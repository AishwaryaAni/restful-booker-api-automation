package com.qa.api.tests;

import com.qa.api.base.BaseApiTest;
import com.qa.api.config.ConfigReader;
import com.qa.api.models.AuthRequest;
import com.qa.api.models.AuthResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Epic("Restful-Booker API")
@Feature("Authentication")
public class AuthTest extends BaseApiTest {

    @Test(groups = {"smoke", "regression"})
    @Severity(SeverityLevel.BLOCKER)
    @Description("Valid admin credentials return a non-empty token")
    public void validCredentialsReturnToken() {
        AuthRequest admin = new AuthRequest(ConfigReader.get("auth.username"), ConfigReader.get("auth.password"));

        AuthResponse response = authClient.createToken(admin)
                .then().statusCode(200)
                .extract().as(AuthResponse.class);

        Assert.assertNotNull(response.token(), "Token should be present");
        Assert.assertFalse(response.token().isBlank(), "Token should not be blank");
        Assert.assertNull(response.reason(), "No failure reason expected on success");
    }

    @DataProvider(name = "badCredentials")
    public Object[][] badCredentials() {
        return new Object[][]{
                {"admin", "wrong-password"},
                {"not-a-user", "password123"},
                {"", ""},
        };
    }

    /**
     * API behaviour note: Restful-Booker answers bad credentials with HTTP 200 + a "reason"
     * instead of 401. The test pins that real behaviour and verifies no token leaks.
     */
    @Test(dataProvider = "badCredentials", groups = {"regression"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("Invalid credentials never return a token")
    public void invalidCredentialsAreRejected(String username, String password) {
        AuthResponse response = authClient.createToken(new AuthRequest(username, password))
                .then().statusCode(200)
                .extract().as(AuthResponse.class);

        Assert.assertNull(response.token(), "No token must be issued for bad credentials");
        Assert.assertEquals(response.reason(), "Bad credentials", "Failure reason");
    }
}
