package com.qa.api.tests;

import com.qa.api.base.BaseApiTest;
import com.qa.api.client.SpecFactory;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.lessThan;

@Epic("Restful-Booker API")
@Feature("Health check")
public class HealthCheckTest extends BaseApiTest {

    @Test(groups = {"smoke", "regression"})
    @Severity(SeverityLevel.BLOCKER)
    @Description("Post-deployment sanity: the service is up and responding within SLA")
    public void serviceIsUp() {
        given().spec(SpecFactory.baseSpec())
                .when().get("/ping")
                .then()
                .statusCode(201) // Restful-Booker's documented response for /ping
                .time(lessThan(slaMs));
    }
}
