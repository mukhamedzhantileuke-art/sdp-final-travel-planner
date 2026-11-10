package kz.aitu.sdp.finalproject.strategy;

import kz.aitu.sdp.finalproject.factory.BookingItem;
import kz.aitu.sdp.finalproject.model.Location;

import java.util.List;

/**
 * Strategy Interface in Strategy Pattern (Student 2).
 * Defines interchangeable algorithmic formulas for optimizing an itinerary.
 */
public interface RouteOptimizationStrategy {

    /**
     * Calculates an optimized travel plan according to specific traveler preferences.
     *
     * @param origin departure city
     * @param destination target city
     * @param candidateBookings available booking options
     * @param budgetLimit maximum customer budget
     * @return optimized plan containing filtered items and strategic metrics
     */
    OptimizedPlan optimizeRoute(Location origin, Location destination, List<BookingItem> candidateBookings, double budgetLimit);

    /**
     * Human-readable title of this strategy formula.
     */
    String getName();
}
