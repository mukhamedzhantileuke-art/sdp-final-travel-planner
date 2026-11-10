package kz.aitu.sdp.finalproject.factory;

/**
 * Concrete Creator for Hotel room bookings (Student 1).
 */
public class HotelBookingFactory extends BookingFactory {

    @Override
    public BookingItem createBooking(String id, String hotelName, String roomType, double pricePerNight, double nights) {
        int numberOfNights = (int) Math.max(1, nights);
        return new HotelBookingItem(id, hotelName, roomType, numberOfNights, pricePerNight);
    }
}
