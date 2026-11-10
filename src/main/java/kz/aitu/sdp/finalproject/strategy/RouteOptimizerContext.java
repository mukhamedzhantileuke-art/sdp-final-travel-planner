package kz.aitu.sdp.finalproject.strategy;

import kz.aitu.sdp.finalproject.factory.BookingItem;
import kz.aitu.sdp.finalproject.model.Location;

import java.util.List;
import java.util.Objects;

/**
 * Context in the Strategy Pattern (Student 2).
 *
 * Maintains a reference to one of the concrete {@link RouteOptimizationStrategy} objects
 * and delegates optimization computations without containing any conditional switch or if-else statements.
 */
public class RouteOptimizerContext {

    private RouteOptimizationStrategy strategy;

    public RouteOptimizerContext(RouteOptimizationStrategy initialStrategy) {
        this.strategy = Objects.requireNonNull(initialStrategy, "initialStrategy cannot be null");
    }

    /**
     * Swaps the active algorithm dynamically at runtime.
     */
    public void setStrategy(RouteOptimizationStrategy newStrategy) {
        this.strategy = Objects.requireNonNull(newStrategy, "strategy cannot be null");
    }

    /**
     * Delegates optimization computation to the currently configured strategy.
     */
    public OptimizedPlan executeOptimization(Location origin, Location destination, List<BookingItem> candidateBookings, double budgetLimit) {
        return strategy.optimizeRoute(origin, destination, candidateBookings, budgetLimit);
    }

    public RouteOptimizationStrategy getStrategy() {
        return strategy;
    }
}
