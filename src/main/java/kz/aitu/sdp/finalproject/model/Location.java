package kz.aitu.sdp.finalproject.model;

import java.util.Objects;

/**
 * Immutable domain model representing a geographic location/airport.
 */
public record Location(
        String city,
        String country,
        String airportCode
) {
    public Location {
        Objects.requireNonNull(city, "City cannot be null");
        Objects.requireNonNull(country, "Country cannot be null");
        Objects.requireNonNull(airportCode, "Airport code cannot be null");
    }

    @Override
    public String toString() {
        return String.format("%s, %s (%s)", city, country, airportCode);
    }
}
