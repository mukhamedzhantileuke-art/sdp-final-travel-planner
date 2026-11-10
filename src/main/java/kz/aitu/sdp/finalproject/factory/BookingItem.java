package kz.aitu.sdp.finalproject.factory;

/**
 * Product interface in the Factory Method Pattern (Student 1).
 * Common abstraction for all reservable travel commodities.
 */
public interface BookingItem {

    String getId();

    String getTitle();

    BookingType getType();

    double getCost();

    String getDetails();

    boolean isConfirmed();

    void confirm();

    void cancel();
}
