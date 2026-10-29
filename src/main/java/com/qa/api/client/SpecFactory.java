package com.qa.api.client;

import com.qa.api.config.ConfigReader;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * One shared request "template": base URL, JSON headers, timeouts, reporting.
 * Manual-testing analogy: the Postman collection's base URL + headers, defined once.
 */
public final class SpecFactory {

    private static final RequestSpecification BASE_SPEC = build();

    private SpecFactory() { }

    public static RequestSpecification baseSpec() {
        return BASE_SPEC;
    }

    private static RequestSpecification build() {
        int timeoutMs = ConfigReader.getInt("http.timeout.ms");
        RestAssuredConfig config = RestAssuredConfig.config().httpClient(
                HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", timeoutMs)
                        .setParam("http.socket.timeout", timeoutMs));

        // Console noise only when an assertion fails — full detail always goes to Allure
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);

        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.get("baseUrl"))
                .setContentType(ContentType.JSON)
                // Exactly "application/json": ContentType.JSON sends 4 MIME types, which this API answers with 418
                .setAccept("application/json")
                .setConfig(config)
                .addFilter(new AllureRestAssured()) // attaches every request/response to the report
                .build();
    }
}
