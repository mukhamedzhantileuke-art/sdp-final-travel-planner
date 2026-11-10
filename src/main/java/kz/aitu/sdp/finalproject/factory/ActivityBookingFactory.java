package kz.aitu.sdp.finalproject.factory;

/**
 * Concrete Creator for Sightseeing & Activity bookings (Student 1).
 */
public class ActivityBookingFactory extends BookingFactory {

    @Override
    public BookingItem createBooking(String id, String activityName, String cityLocation, double ticketPrice, double durationHours) {
        double duration = durationHours > 0 ? durationHours : 2.0;
        return new ActivityBookingItem(id, activityName, cityLocation, duration, ticketPrice);
    }
}
