package kz.aitu.sdp.finalproject.model;

import kz.aitu.sdp.finalproject.factory.BookingItem;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Immutable product class assembled by {@link kz.aitu.sdp.finalproject.builder.TripItineraryBuilder}.
 * Encapsulates the entire multi-day travel schedule, budget, bookings, and customer info.
 */
public class TripItinerary {

    private final String id;
    private final String tripName;
    private final Traveler traveler;
    private final Location origin;
    private final Location destination;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final double budgetLimit;
    private final List<BookingItem> bookings;
    private final List<String> plannedNotes;
    private final TripStatus status;

    public TripItinerary(String id,
                         String tripName,
                         Traveler traveler,
                         Location origin,
                         Location destination,
                         LocalDate startDate,
                         LocalDate endDate,
                         double budgetLimit,
                         List<BookingItem> bookings,
                         List<String> plannedNotes,
                         TripStatus status) {
        this.id = Objects.requireNonNull(id, "Itinerary ID cannot be null");
        this.tripName = Objects.requireNonNull(tripName, "Trip name cannot be null");
        this.traveler = Objects.requireNonNull(traveler, "Traveler cannot be null");
        this.origin = Objects.requireNonNull(origin, "Origin cannot be null");
        this.destination = Objects.requireNonNull(destination, "Destination cannot be null");
        this.startDate = Objects.requireNonNull(startDate, "Start date cannot be null");
        this.endDate = Objects.requireNonNull(endDate, "End date cannot be null");
        this.budgetLimit = budgetLimit;
        this.bookings = bookings != null ? List.copyOf(bookings) : List.of();
        this.plannedNotes = plannedNotes != null ? List.copyOf(plannedNotes) : List.of();
        this.status = status != null ? status : TripStatus.DRAFT;
    }

    public double getTotalCost() {
        return bookings.stream().mapToDouble(BookingItem::getCost).sum();
    }

    public long getDurationDays() {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    public boolean isWithinBudget() {
        return budgetLimit <= 0 || getTotalCost() <= budgetLimit;
    }

    public TripItinerary withStatus(TripStatus newStatus) {
        return new TripItinerary(id, tripName, traveler, origin, destination,
                startDate, endDate, budgetLimit, bookings, plannedNotes, newStatus);
    }

    public String getId() {
        return id;
    }

    public String getTripName() {
        return tripName;
    }

    public Traveler getTraveler() {
        return traveler;
    }

    public Location getOrigin() {
        return origin;
    }

    public Location getDestination() {
        return destination;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getBudgetLimit() {
        return budgetLimit;
    }

    public List<BookingItem> getBookings() {
        return Collections.unmodifiableList(bookings);
    }

    public List<String> getPlannedNotes() {
        return Collections.unmodifiableList(plannedNotes);
    }

    public TripStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format(java.util.Locale.US, "TripItinerary{id='%s', name='%s', %s -> %s, duration=%d days, cost=$%.2f/budget=$%.2f, status=%s}",
                id, tripName, origin.city(), destination.city(), getDurationDays(), getTotalCost(), budgetLimit, status);
    }
}
