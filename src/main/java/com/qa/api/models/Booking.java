package com.qa.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Booking payload — used for both request bodies and response bodies.
 * Java records give equals() for free, so "response == what I sent" is one assertion.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Booking(
        String firstname,
        String lastname,
        Integer totalprice,
        Boolean depositpaid,
        BookingDates bookingdates,
        String additionalneeds) {

    /** Copy with a new first name — handy for update tests. */
    public Booking withFirstname(String newFirstname) {
        return new Booking(newFirstname, lastname, totalprice, depositpaid, bookingdates, additionalneeds);
    }
}
