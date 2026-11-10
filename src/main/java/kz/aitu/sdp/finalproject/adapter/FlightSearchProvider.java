package kz.aitu.sdp.finalproject.adapter;

import java.time.LocalDate;
import java.util.List;

/**
 * Target Interface in the Adapter Pattern (Student 2).
 * Defines the clean, modern domain contract expected by our travel planner application.
 */
public interface FlightSearchProvider {

    /**
     * Searches for active flights matching the origin, destination, and departure date.
     */
    List<FlightOption> searchAvailableFlights(String originAirport, String destinationAirport, LocalDate departureDate);
}
