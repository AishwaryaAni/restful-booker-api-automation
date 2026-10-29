package com.qa.api.data;

import com.qa.api.models.Booking;
import com.qa.api.models.BookingDates;
import net.datafaker.Faker;

import java.time.LocalDate;

/**
 * Generates fresh, valid test data per test — no hard-coded rows,
 * no collisions with other people using the same public API.
 */
public final class BookingDataFactory {

    private static final Faker FAKER = new Faker();
    private static final String[] NEEDS = {"Breakfast", "Late checkout", "Airport pickup", "Extra pillow", "Dinner"};

    private BookingDataFactory() { }

    public static Booking validBooking() {
        LocalDate checkin = LocalDate.now().plusDays(FAKER.number().numberBetween(1, 60));
        LocalDate checkout = checkin.plusDays(FAKER.number().numberBetween(1, 14));
        return new Booking(
                FAKER.name().firstName(),
                FAKER.name().lastName(),
                FAKER.number().numberBetween(50, 5000),
                FAKER.bool().bool(),
                new BookingDates(checkin.toString(), checkout.toString()),
                FAKER.options().option(NEEDS));
    }

    public static String randomFirstName() {
        return FAKER.name().firstName();
    }
}
