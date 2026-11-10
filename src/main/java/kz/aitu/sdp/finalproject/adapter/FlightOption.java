package kz.aitu.sdp.finalproject.adapter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Clean target domain model representing a flight offer.
 */
public record FlightOption(
        String flightNumber,
        String airline,
        String originAirport,
        String destinationAirport,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        double priceUsd,
        int durationMinutes
) {
    public FlightOption {
        Objects.requireNonNull(flightNumber, "flightNumber cannot be null");
        Objects.requireNonNull(airline, "airline cannot be null");
        Objects.requireNonNull(originAirport, "originAirport cannot be null");
        Objects.requireNonNull(destinationAirport, "destinationAirport cannot be null");
        Objects.requireNonNull(departureTime, "departureTime cannot be null");
        Objects.requireNonNull(arrivalTime, "arrivalTime cannot be null");
    }

    @Override
    public String toString() {
        return String.format("%s (%s): %s -> %s | Departure: %s | $%.2f (%d mins)",
                flightNumber, airline, originAirport, destinationAirport, departureTime, priceUsd, durationMinutes);
    }
}
