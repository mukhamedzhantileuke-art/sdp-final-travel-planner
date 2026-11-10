package kz.aitu.sdp.finalproject.strategy;

import kz.aitu.sdp.finalproject.factory.BookingItem;
import kz.aitu.sdp.finalproject.model.Location;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Concrete Strategy 2 (Student 2): Cheapest Route Strategy.
 * Minimizes total trip expenditure. Sorts candidate items by cost and selects
 * budget-friendly options while ensuring the total remains within the budget cap.
 */
public class CheapestRouteStrategy implements RouteOptimizationStrategy {

    @Override
    public OptimizedPlan optimizeRoute(Location origin, Location destination, List<BookingItem> candidateBookings, double budgetLimit) {
        List<BookingItem> sortedCandidates = new ArrayList<>(candidateBookings);
        sortedCandidates.sort(Comparator.comparingDouble(BookingItem::getCost));

        List<BookingItem> chosen = new ArrayList<>();
        double accumulatedCost = 0.0;

        for (BookingItem item : sortedCandidates) {
            if (budgetLimit <= 0 || (accumulatedCost + item.getCost()) <= budgetLimit) {
                chosen.add(item);
                accumulatedCost += item.getCost();
            }
        }

        double estimatedDuration = 14.0; // Slower budget transit/indirect routes
        String rationale = String.format("Cheapest options selected for %s -> %s. Total cost capped at $%.2f (Under budget: %b).",
                origin.city(), destination.city(), accumulatedCost, budgetLimit <= 0 || accumulatedCost <= budgetLimit);

        return new OptimizedPlan(getName(), chosen, accumulatedCost, estimatedDuration, rationale);
    }

    @Override
    public String getName() {
        return "Cheapest Route (Budget Priority)";
    }
}
