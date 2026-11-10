package kz.aitu.sdp.finalproject.model;

import java.util.Objects;

/**
 * Immutable domain model representing a traveler/customer.
 */
public record Traveler(
        String name,
        String email,
        String phoneNumber,
        String passportNumber
) {
    public Traveler {
        Objects.requireNonNull(name, "Traveler name cannot be null");
        Objects.requireNonNull(email, "Traveler email cannot be null");
        Objects.requireNonNull(phoneNumber, "Traveler phone number cannot be null");
        Objects.requireNonNull(passportNumber, "Traveler passport cannot be null");
    }
}
