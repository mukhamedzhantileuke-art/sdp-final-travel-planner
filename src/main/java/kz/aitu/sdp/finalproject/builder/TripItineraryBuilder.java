package kz.aitu.sdp.finalproject.builder;

import kz.aitu.sdp.finalproject.factory.BookingItem;
import kz.aitu.sdp.finalproject.model.Location;
import kz.aitu.sdp.finalproject.model.Traveler;
import kz.aitu.sdp.finalproject.model.TripItinerary;
import kz.aitu.sdp.finalproject.model.TripStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Builder Pattern implementation (Student 1).
 *
 * Provides a fluent, stepwise construction process for {@link TripItinerary}.
 * Solves the telescoping constructor anti-pattern and enforces domain invariants
 * before instantiation.
 */
public class TripItineraryBuilder {

    private String id;
    private String tripName;
    private Traveler traveler;
    private Location origin;
    private Location destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private double budgetLimit;
    private final List<BookingItem> bookings = new ArrayList<>();
    private final List<String> plannedNotes = new ArrayList<>();
    private TripStatus status = TripStatus.DRAFT;

    public TripItineraryBuilder() {
        this.id = "ITIN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public TripItineraryBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public TripItineraryBuilder setTripName(String tripName) {
        this.tripName = tripName;
        return this;
    }

    public TripItineraryBuilder setTraveler(Traveler traveler) {
        this.traveler = traveler;
        return this;
    }

    public TripItineraryBuilder setOrigin(Location origin) {
        this.origin = origin;
        return this;
    }

    public TripItineraryBuilder setDestination(Location destination) {
        this.destination = destination;
        return this;
    }

    public TripItineraryBuilder setDateRange(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        return this;
    }

    public TripItineraryBuilder setBudgetLimit(double budgetLimit) {
        if (budgetLimit < 0) {
            throw new IllegalArgumentException("Budget limit cannot be negative");
        }
        this.budgetLimit = budgetLimit;
        return this;
    }

    public TripItineraryBuilder addBooking(BookingItem booking) {
        if (booking != null) {
            this.bookings.add(booking);
        }
        return this;
    }

    public TripItineraryBuilder addBookings(List<BookingItem> bookingsList) {
        if (bookingsList != null) {
            this.bookings.addAll(bookingsList);
        }
        return this;
    }

    public TripItineraryBuilder addNote(String note) {
        if (note != null && !note.isBlank()) {
            this.plannedNotes.add(note.trim());
        }
        return this;
    }

    public TripItineraryBuilder setStatus(TripStatus status) {
        this.status = status;
        return this;
    }

    /**
     * Builds and validates the immutable TripItinerary object.
     */
    public TripItinerary build() {
        if (tripName == null || tripName.isBlank()) {
            throw new IllegalStateException("Trip name must be provided");
        }
        if (traveler == null) {
            throw new IllegalStateException("Traveler details must be provided");
        }
        if (origin == null || destination == null) {
            throw new IllegalStateException("Both origin and destination locations must be provided");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalStateException("Both start and end dates must be specified");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalStateException("End date cannot be earlier than start date");
        }

        return new TripItinerary(
                id,
                tripName,
                traveler,
                origin,
                destination,
                startDate,
                endDate,
                budgetLimit,
                new ArrayList<>(bookings),
                new ArrayList<>(plannedNotes),
                status
        );
    }

    public void reset() {
        this.id = "ITIN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.tripName = null;
        this.traveler = null;
        this.origin = null;
        this.destination = null;
        this.startDate = null;
        this.endDate = null;
        this.budgetLimit = 0.0;
        this.bookings.clear();
        this.plannedNotes.clear();
        this.status = TripStatus.DRAFT;
    }
}
