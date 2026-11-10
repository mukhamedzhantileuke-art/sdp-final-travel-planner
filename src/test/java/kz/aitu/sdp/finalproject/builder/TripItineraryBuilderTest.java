package kz.aitu.sdp.finalproject.builder;

import kz.aitu.sdp.finalproject.factory.FlightBookingItem;
import kz.aitu.sdp.finalproject.model.Location;
import kz.aitu.sdp.finalproject.model.Traveler;
import kz.aitu.sdp.finalproject.model.TripItinerary;
import kz.aitu.sdp.finalproject.model.TripStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pattern 1 Tests: Builder Pattern (Student 1)")
class TripItineraryBuilderTest {

    private Traveler traveler;
    private Location origin;
    private Location destination;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        traveler = new Traveler("Alice Smith", "alice@example.com", "+1-555-0100", "P9876543");
        origin = new Location("New York", "USA", "JFK");
        destination = new Location("Tokyo", "Japan", "HND");
        startDate = LocalDate.of(2027, 4, 1);
        endDate = LocalDate.of(2027, 4, 10);
    }

    @Test
    @DisplayName("Test 1: Builder successfully constructs valid immutable TripItinerary")
    void testSuccessfulItineraryBuild() {
        TripItinerary itinerary = new TripItineraryBuilder()
                .setId("ITIN-TEST-01")
                .setTripName("Tokyo Cherry Blossom")
                .setTraveler(traveler)
                .setOrigin(origin)
                .setDestination(destination)
                .setDateRange(startDate, endDate)
                .setBudgetLimit(3500.0)
                .addBooking(new FlightBookingItem("FL-1", "JL-005", "Japan Airlines", "Economy", 1200.0))
                .addNote("Vegetarian meal requested")
                .setStatus(TripStatus.DRAFT)
                .build();

        assertNotNull(itinerary);
        assertEquals("ITIN-TEST-01", itinerary.getId());
        assertEquals("Tokyo Cherry Blossom", itinerary.getTripName());
        assertEquals(9, itinerary.getDurationDays());
        assertEquals(1200.0, itinerary.getTotalCost());
        assertTrue(itinerary.isWithinBudget());
        assertEquals(1, itinerary.getBookings().size());
        assertEquals(1, itinerary.getPlannedNotes().size());
        assertEquals(TripStatus.DRAFT, itinerary.getStatus());
    }

    @Test
    @DisplayName("Test 2: Builder throws IllegalStateException if mandatory dates or destinations missing")
    void testBuilderValidationMissingFieldsThrowsException() {
        TripItineraryBuilder builder = new TripItineraryBuilder()
                .setTripName("Incomplete Trip")
                .setTraveler(traveler)
                .setOrigin(origin);
                // Missing destination and dates

        IllegalStateException ex = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(ex.getMessage().contains("destination") || ex.getMessage().contains("dates"));
    }

    @Test
    @DisplayName("Test 3: Builder rejects inverted date range where end date is before start date")
    void testInvertedDatesThrowsException() {
        TripItineraryBuilder builder = new TripItineraryBuilder()
                .setTripName("Invalid Dates Trip")
                .setTraveler(traveler)
                .setOrigin(origin)
                .setDestination(destination)
                .setDateRange(LocalDate.of(2027, 5, 10), LocalDate.of(2027, 5, 1)); // Inverted!

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    @DisplayName("Test 4: Builder rejects negative budget limits")
    void testNegativeBudgetThrowsException() {
        TripItineraryBuilder builder = new TripItineraryBuilder();
        assertThrows(IllegalArgumentException.class, () -> builder.setBudgetLimit(-100.0));
    }
}
