package kz.aitu.sdp.finalproject.strategy;

import kz.aitu.sdp.finalproject.factory.ActivityBookingItem;
import kz.aitu.sdp.finalproject.factory.BookingItem;
import kz.aitu.sdp.finalproject.factory.FlightBookingItem;
import kz.aitu.sdp.finalproject.factory.HotelBookingItem;
import kz.aitu.sdp.finalproject.model.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pattern 5 Tests: Strategy Pattern (Student 2)")
class RouteOptimizationStrategyTest {

    private Location origin;
    private Location destination;
    private List<BookingItem> candidateBookings;

    @BeforeEach
    void setUp() {
        origin = new Location("Astana", "Kazakhstan", "NQZ");
        destination = new Location("Istanbul", "Turkey", "IST");

        BookingItem flight1 = new FlightBookingItem("F1", "KC-915", "Air Astana", "Economy", 350.0);
        BookingItem flight2 = new FlightBookingItem("F2", "TK-355", "Turkish Airlines", "Economy", 420.0);
        BookingItem hotel = new HotelBookingItem("H1", "Bosphorus View Hotel", "Standard", 3, 100.0); // 300.0
        BookingItem excursion1 = new ActivityBookingItem("A1", "Hagia Sophia Guided Tour", "Istanbul", 3.0, 40.0);
        BookingItem excursion2 = new ActivityBookingItem("A2", "Bosphorus Sunset Yacht Cruise", "Istanbul", 2.5, 60.0);

        candidateBookings = List.of(flight1, flight2, hotel, excursion1, excursion2);
    }

    @Test
    @DisplayName("Test 15: FastestRouteStrategy optimizes for rapid transit and minimal duration")
    void testFastestRouteStrategy() {
        RouteOptimizerContext context = new RouteOptimizerContext(new FastestRouteStrategy());
        OptimizedPlan plan = context.executeOptimization(origin, destination, candidateBookings, 1500.0);

        assertNotNull(plan);
        assertTrue(plan.strategyName().contains("Fastest"));
        assertEquals(7.5, plan.estimatedTravelDurationHours(), 0.001);
        assertTrue(plan.selectedBookings().stream().anyMatch(b -> b instanceof FlightBookingItem));
    }

    @Test
    @DisplayName("Test 16: CheapestRouteStrategy sorts and respects budget ceiling")
    void testCheapestRouteStrategy() {
        RouteOptimizerContext context = new RouteOptimizerContext(new CheapestRouteStrategy());
        double budgetCap = 500.0;
        OptimizedPlan plan = context.executeOptimization(origin, destination, candidateBookings, budgetCap);

        assertNotNull(plan);
        assertTrue(plan.strategyName().contains("Cheapest"));
        assertTrue(plan.totalCost() <= budgetCap, "Total cost must not exceed budget cap");
    }

    @Test
    @DisplayName("Test 17: ScenicRouteStrategy prioritizes excursions and sightseeing")
    void testScenicRouteStrategy() {
        RouteOptimizerContext context = new RouteOptimizerContext(new ScenicRouteStrategy());
        OptimizedPlan plan = context.executeOptimization(origin, destination, candidateBookings, 2000.0);

        assertNotNull(plan);
        assertTrue(plan.strategyName().contains("Scenic"));
        long activityCount = plan.selectedBookings().stream().filter(b -> b instanceof ActivityBookingItem).count();
        assertEquals(2, activityCount, "Scenic route must include cultural excursions");
    }

    @Test
    @DisplayName("Test 18: Context allows runtime swapping of strategies without branching")
    void testRuntimeStrategySwapping() {
        RouteOptimizerContext context = new RouteOptimizerContext(new FastestRouteStrategy());
        assertEquals("Fastest Route (Speed Priority)", context.getStrategy().getName());

        context.setStrategy(new CheapestRouteStrategy());
        assertEquals("Cheapest Route (Budget Priority)", context.getStrategy().getName());

        context.setStrategy(new ScenicRouteStrategy());
        assertEquals("Scenic Route (Experience & Tourism Priority)", context.getStrategy().getName());
    }
}
