package com.qa.api.base;

import com.qa.api.client.AuthClient;
import com.qa.api.client.BookingClient;
import com.qa.api.config.ConfigReader;
import com.qa.api.data.BookingDataFactory;
import com.qa.api.models.Booking;
import com.qa.api.models.BookingResponse;

/** Shared clients and helpers for every API test class. */
public abstract class BaseApiTest {

    protected final BookingClient bookingClient = new BookingClient();
    protected final AuthClient authClient = new AuthClient();

    /** Max acceptable response time (SLA) in ms — configurable per environment. */
    protected final long slaMs = ConfigReader.getInt("sla.response.ms");

    protected String token() {
        return AuthClient.adminToken();
    }

    /**
     * Test-data setup through the API itself (precondition step of a manual test case).
     * Every test creates its own booking, so tests never depend on each other.
     */
    protected BookingResponse createFreshBooking() {
        Booking booking = BookingDataFactory.validBooking();
        return bookingClient.create(booking)
                .then().statusCode(200)
                .extract().as(BookingResponse.class);
    }
}
