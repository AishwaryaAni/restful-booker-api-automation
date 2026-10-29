package com.qa.api.tests;

import com.qa.api.base.BaseApiTest;
import com.qa.api.data.BookingDataFactory;
import com.qa.api.models.Booking;
import com.qa.api.models.BookingResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.lessThan;

@Epic("Restful-Booker API")
@Feature("Booking CRUD")
public class BookingCrudTest extends BaseApiTest {

    @Test(groups = {"smoke", "regression"})
    @Severity(SeverityLevel.BLOCKER)
    @Description("POST /booking stores exactly what was sent and returns a new id within SLA")
    public void createBooking() {
        Booking request = BookingDataFactory.validBooking();

        BookingResponse response = bookingClient.create(request)
                .then()
                .statusCode(200)
                .time(lessThan(slaMs))
                .body(matchesJsonSchemaInClasspath("schemas/create-booking-response.json"))
                .extract().as(BookingResponse.class);

        Assert.assertNotNull(response.bookingid(), "Booking id");
        Assert.assertTrue(response.bookingid() > 0, "Booking id should be positive");
        Assert.assertEquals(response.booking(), request, "Stored booking equals request payload");
    }

    @Test(groups = {"smoke", "regression"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("GET /booking/{id} returns the booking and honours the JSON contract")
    public void getBookingById() {
        BookingResponse created = createFreshBooking();

        Booking fetched = bookingClient.getById(created.bookingid())
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/booking.json"))
                .extract().as(Booking.class);

        Assert.assertEquals(fetched, created.booking(), "Fetched booking equals created booking");
    }

    @Test(groups = {"regression"})
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /booking lists ids and includes a newly created booking")
    public void listIncludesNewBooking() {
        BookingResponse created = createFreshBooking();

        List<Integer> ids = bookingClient.getAllIds()
                .then().statusCode(200)
                .extract().jsonPath().getList("bookingid", Integer.class);

        Assert.assertTrue(ids.contains(created.bookingid()), "New booking id should appear in the list");
    }

    @Test(groups = {"regression"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("PUT /booking/{id} with a valid token replaces the whole booking")
    public void fullUpdateBooking() {
        BookingResponse created = createFreshBooking();
        Booking replacement = BookingDataFactory.validBooking();

        Booking updated = bookingClient.update(created.bookingid(), replacement, token())
                .then().statusCode(200)
                .extract().as(Booking.class);

        Assert.assertEquals(updated, replacement, "PUT response equals replacement payload");

        // Verify persistence, not just the echo — the "re-open the record" step of a manual test
        Booking reFetched = bookingClient.getById(created.bookingid()).then().statusCode(200).extract().as(Booking.class);
        Assert.assertEquals(reFetched, replacement, "Change persisted");
    }

    @Test(groups = {"regression"})
    @Severity(SeverityLevel.NORMAL)
    @Description("PATCH /booking/{id} changes only the sent field and leaves the rest untouched")
    public void partialUpdateBooking() {
        BookingResponse created = createFreshBooking();
        String newFirstName = BookingDataFactory.randomFirstName();

        Booking patched = bookingClient.partialUpdate(created.bookingid(), Map.of("firstname", newFirstName), token())
                .then().statusCode(200)
                .extract().as(Booking.class);

        Assert.assertEquals(patched, created.booking().withFirstname(newFirstName),
                "Only firstname changed; all other fields identical");
    }

    @Test(groups = {"regression"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("DELETE /booking/{id} removes the booking; a follow-up GET returns 404")
    public void deleteBooking() {
        BookingResponse created = createFreshBooking();

        Response delete = bookingClient.delete(created.bookingid(), token());
        Assert.assertEquals(delete.statusCode(), 201, "Restful-Booker returns 201 on successful delete");

        bookingClient.getById(created.bookingid()).then().statusCode(404);
    }
}
