package kz.aitu.sdp.finalproject.model;

import java.util.Objects;

/**
 * Result of executing the entire trip checkout via TravelBookingFacade.
 */
public record BookingResult(
        boolean success,
        String itineraryId,
        String confirmationCode,
        double totalAmountCharged,
        String message,
        long timestamp
) {
    public BookingResult {
        Objects.requireNonNull(itineraryId, "Itinerary ID cannot be null");
        Objects.requireNonNull(confirmationCode, "Confirmation code cannot be null");
        Objects.requireNonNull(message, "Message cannot be null");
    }

    public static BookingResult success(String itineraryId, String confirmationCode, double amount, String message) {
        return new BookingResult(true, itineraryId, confirmationCode, amount, message, System.currentTimeMillis());
    }

    public static BookingResult failure(String itineraryId, String reason) {
        return new BookingResult(false, itineraryId, "NONE", 0.0, reason, System.currentTimeMillis());
    }
}
