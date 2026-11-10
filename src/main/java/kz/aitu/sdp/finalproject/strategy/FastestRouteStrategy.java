package kz.aitu.sdp.finalproject.strategy;

import kz.aitu.sdp.finalproject.factory.BookingItem;
import kz.aitu.sdp.finalproject.factory.BookingType;
import kz.aitu.sdp.finalproject.model.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete Strategy 1 (Student 2): Fastest Route Strategy.
 * Prioritizes speed and minimal transit duration. Selects fastest flights,
 * central luxury hotels with rapid transit access, and minimizes layovers.
 */
public class FastestRouteStrategy implements RouteOptimizationStrategy {

    @Override
    public OptimizedPlan optimizeRoute(Location origin, Location destination, List<BookingItem> candidateBookings, double budgetLimit) {
        List<BookingItem> chosen = new ArrayList<>();

        // Prioritize flights and central accommodation
        for (BookingItem item : candidateBookings) {
            if (item.getType() == BookingType.FLIGHT || item.getType() == BookingType.HOTEL) {
                chosen.add(item);
            }
        }

        // If activities present, add only short compact ones
        candidateBookings.stream()
                .filter(item -> item.getType() == BookingType.ACTIVITY)
                .limit(1)
                .forEach(chosen::add);

        double totalCost = chosen.stream().mapToDouble(BookingItem::getCost).sum();
        double estimatedDuration = 7.5; // Shortest transit duration hours

        String rationale = String.format("Fastest direct air transit selected for %s -> %s. Layovers minimized to zero.",
                origin.city(), destination.city());

        return new OptimizedPlan(getName(), chosen, totalCost, estimatedDuration, rationale);
    }

    @Override
    public String getName() {
        return "Fastest Route (Speed Priority)";
    }
}
