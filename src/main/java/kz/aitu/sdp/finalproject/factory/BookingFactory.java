package kz.aitu.sdp.finalproject.factory;

/**
 * Creator base class for Factory Method Pattern (Student 1).
 * Declares the factory method that returns a {@link BookingItem}.
 */
public abstract class BookingFactory {

    /**
     * Factory method to be overridden by concrete creators.
     */
    public abstract BookingItem createBooking(String id, String primaryTitle, String secondaryOption, double costOrRate, double extraNumeric);

    /**
     * Common template logic operating on the created product.
     */
    public BookingItem issueAndLogBooking(String id, String primaryTitle, String secondaryOption, double costOrRate, double extraNumeric) {
        BookingItem item = createBooking(id, primaryTitle, secondaryOption, costOrRate, extraNumeric);
        return item;
    }
}
