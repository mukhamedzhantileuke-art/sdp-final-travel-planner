package kz.aitu.sdp.finalproject.factory;

import java.util.Objects;

/**
 * Concrete Product 3 (Student 1): Sightseeing tour or excursion ticket.
 */
public class ActivityBookingItem implements BookingItem {

    private final String id;
    private final String activityName;
    private final String cityLocation;
    private final double durationHours;
    private final double price;
    private boolean confirmed;

    public ActivityBookingItem(String id, String activityName, String cityLocation, double durationHours, double price) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.activityName = Objects.requireNonNull(activityName, "activityName cannot be null");
        this.cityLocation = Objects.requireNonNull(cityLocation, "cityLocation cannot be null");
        this.durationHours = durationHours;
        this.price = price;
        this.confirmed = false;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return String.format("Excursion: %s", activityName);
    }

    @Override
    public BookingType getType() {
        return BookingType.ACTIVITY;
    }

    @Override
    public double getCost() {
        return price;
    }

    @Override
    public String getDetails() {
        return String.format(java.util.Locale.US, "Activity: %s in %s, Duration: %.1fh, Ticket Price: $%.2f",
                activityName, cityLocation, durationHours, price);
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

    public String getActivityName() {
        return activityName;
    }

    public String getCityLocation() {
        return cityLocation;
    }

    public double getDurationHours() {
        return durationHours;
    }
}
