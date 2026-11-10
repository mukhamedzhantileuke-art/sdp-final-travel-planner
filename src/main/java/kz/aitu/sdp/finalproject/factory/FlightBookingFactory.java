package kz.aitu.sdp.finalproject.factory;

/**
 * Concrete Creator for Flight bookings (Student 1).
 */
public class FlightBookingFactory extends BookingFactory {

    @Override
    public BookingItem createBooking(String id, String flightNumber, String airline, double ticketPrice, double unused) {
        return new FlightBookingItem(id, flightNumber, airline, "Economy Plus", ticketPrice);
    }

    public BookingItem createCustomFlight(String id, String flightNumber, String airline, String seatClass, double ticketPrice) {
        return new FlightBookingItem(id, flightNumber, airline, seatClass, ticketPrice);
    }
}
