package com.qa.api.client;

import com.qa.api.models.Booking;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * One method per /booking endpoint. Returns the raw Response so each TEST decides
 * what to assert — the client never passes or fails anything itself.
 */
public class BookingClient {

    private static final String BOOKING = "/booking";
    private static final String BOOKING_BY_ID = "/booking/{id}";

    @Step("POST /booking")
    public Response create(Booking booking) {
        return request().body(booking).when().post(BOOKING);
    }

    /** Sends an arbitrary (possibly invalid) body — for negative / validation tests. */
    @Step("POST /booking with raw payload")
    public Response partialCreate(Map<String, Object> rawBody) {
        return request().body(rawBody).when().post(BOOKING);
    }

    @Step("GET /booking/{id}")
    public Response getById(int id) {
        return request().pathParam("id", id).when().get(BOOKING_BY_ID);
    }

    @Step("GET /booking (all ids)")
    public Response getAllIds() {
        return request().when().get(BOOKING);
    }

    @Step("PUT /booking/{id}")
    public Response update(int id, Booking booking, String token) {
        return authorised(token).pathParam("id", id).body(booking).when().put(BOOKING_BY_ID);
    }

    @Step("PATCH /booking/{id}")
    public Response partialUpdate(int id, Map<String, Object> fields, String token) {
        return authorised(token).pathParam("id", id).body(fields).when().patch(BOOKING_BY_ID);
    }

    @Step("DELETE /booking/{id}")
    public Response delete(int id, String token) {
        return authorised(token).pathParam("id", id).when().delete(BOOKING_BY_ID);
    }

    @Step("PUT /booking/{id} without auth")
    public Response updateWithoutAuth(int id, Booking booking) {
        return request().pathParam("id", id).body(booking).when().put(BOOKING_BY_ID);
    }

    private RequestSpecification request() {
        return given().spec(SpecFactory.baseSpec());
    }

    /** Restful-Booker reads the token from a cookie named "token". */
    private RequestSpecification authorised(String token) {
        return request().cookie("token", token);
    }
}
