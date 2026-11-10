package kz.aitu.sdp.finalproject.strategy;

import kz.aitu.sdp.finalproject.factory.BookingItem;
import kz.aitu.sdp.finalproject.factory.BookingType;
import kz.aitu.sdp.finalproject.model.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete Strategy 3 (Student 2): Scenic Route Strategy.
 * Prioritizes tourist immersion, scenic sightseeing, and cultural excursions.
 * Maximizes planned activities and scenic daylight transfers.
 */
public class ScenicRouteStrategy implements RouteOptimizationStrategy {

    @Override
    public OptimizedPlan optimizeRoute(Location origin, Location destination, List<BookingItem> candidateBookings, double budgetLimit) {
        List<BookingItem> chosen = new ArrayList<>();

        // Add all cultural activities and excursions first
        candidateBookings.stream()
                .filter(b -> b.getType() == BookingType.ACTIVITY)
                .forEach(chosen::add);

        // Add hotels and flights
        candidateBookings.stream()
                .filter(b -> b.getType() == BookingType.HOTEL || b.getType() == BookingType.FLIGHT)
                .forEach(chosen::add);

        double totalCost = chosen.stream().mapToDouble(BookingItem::getCost).sum();
        double estimatedDuration = 18.5; // Longer journey including scenic sightseeing

        String rationale = String.format("Scenic panoramic route for %s -> %s with %d cultural activities and scenic stopovers.",
                origin.city(), destination.city(), chosen.stream().filter(b -> b.getType() == BookingType.ACTIVITY).count());

        return new OptimizedPlan(getName(), chosen, totalCost, estimatedDuration, rationale);
    }

    @Override
    public String getName() {
        return "Scenic Route (Experience & Tourism Priority)";
    }
}
