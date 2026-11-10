package kz.aitu.sdp.finalproject.factory;

import java.util.Objects;

/**
 * Concrete Product 2 (Student 1): Hotel room reservation.
 */
public class HotelBookingItem implements BookingItem {

    private final String id;
    private final String hotelName;
    private final String roomType;
    private final int numberOfNights;
    private final double pricePerNight;
    private boolean confirmed;

    public HotelBookingItem(String id, String hotelName, String roomType, int numberOfNights, double pricePerNight) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.hotelName = Objects.requireNonNull(hotelName, "hotelName cannot be null");
        this.roomType = Objects.requireNonNull(roomType, "roomType cannot be null");
        this.numberOfNights = numberOfNights;
        this.pricePerNight = pricePerNight;
        this.confirmed = false;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return String.format("Hotel Stay at %s", hotelName);
    }

    @Override
    public BookingType getType() {
        return BookingType.HOTEL;
    }

    @Override
    public double getCost() {
        return numberOfNights * pricePerNight;
    }

    @Override
    public String getDetails() {
        return String.format(java.util.Locale.US, "Hotel: %s, Room: %s, Nights: %d @ $%.2f/night, Total: $%.2f",
                hotelName, roomType, numberOfNights, pricePerNight, getCost());
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

    public String getHotelName() {
        return hotelName;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getNumberOfNights() {
        return numberOfNights;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }
}
