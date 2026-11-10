package kz.aitu.sdp.finalproject.factory;

import java.util.Objects;

/**
 * Concrete Product 1 (Student 1): Airline flight seat reservation.
 */
public class FlightBookingItem implements BookingItem {

    private final String id;
    private final String flightNumber;
    private final String airline;
    private final String seatClass;
    private final double cost;
    private boolean confirmed;

    public FlightBookingItem(String id, String flightNumber, String airline, String seatClass, double cost) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.flightNumber = Objects.requireNonNull(flightNumber, "flightNumber cannot be null");
        this.airline = Objects.requireNonNull(airline, "airline cannot be null");
        this.seatClass = Objects.requireNonNull(seatClass, "seatClass cannot be null");
        this.cost = cost;
        this.confirmed = false;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return String.format("Flight %s (%s)", flightNumber, airline);
    }

    @Override
    public BookingType getType() {
        return BookingType.FLIGHT;
    }

    @Override
    public double getCost() {
        return cost;
    }

    @Override
    public String getDetails() {
        return String.format(java.util.Locale.US, "Airline: %s, Flight: %s, Class: %s, Price: $%.2f",
                airline, flightNumber, seatClass, cost);
    }

    @Override
    public boolean isConfirmed() {
        return confirmed;
    }

    @Override
    public void confirm() {
        this.confirmed = true;
    }

    @Override
    public void cancel() {
        this.confirmed = false;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getAirline() {
        return airline;
    }

    public String getSeatClass() {
        return seatClass;
    }
}
