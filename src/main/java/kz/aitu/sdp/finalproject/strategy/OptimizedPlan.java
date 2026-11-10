package kz.aitu.sdp.finalproject.strategy;

import kz.aitu.sdp.finalproject.factory.BookingItem;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Result produced by an itinerary optimization strategy.
 */
public record OptimizedPlan(
        String strategyName,
        List<BookingItem> selectedBookings,
        double totalCost,
        double estimatedTravelDurationHours,
        String rationale
) {
    public OptimizedPlan {
        Objects.requireNonNull(strategyName, "strategyName cannot be null");
        Objects.requireNonNull(rationale, "rationale cannot be null");
        selectedBookings = selectedBookings != null ? List.copyOf(selectedBookings) : List.of();
    }

    public List<BookingItem> selectedBookings() {
        return Collections.unmodifiableList(selectedBookings);
    }
}
