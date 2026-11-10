package kz.aitu.sdp.finalproject;

import kz.aitu.sdp.finalproject.adapter.AmadeusFlightAdapter;
import kz.aitu.sdp.finalproject.adapter.FlightOption;
import kz.aitu.sdp.finalproject.adapter.FlightSearchProvider;
import kz.aitu.sdp.finalproject.adapter.legacy.LegacyAmadeusFlightApi;
import kz.aitu.sdp.finalproject.builder.TripItineraryBuilder;
import kz.aitu.sdp.finalproject.facade.TravelBookingFacade;
import kz.aitu.sdp.finalproject.facade.subsystems.FlightSubsystem;
import kz.aitu.sdp.finalproject.facade.subsystems.HotelSubsystem;
import kz.aitu.sdp.finalproject.facade.subsystems.InsuranceSubsystem;
import kz.aitu.sdp.finalproject.facade.subsystems.PaymentSubsystem;
import kz.aitu.sdp.finalproject.factory.ActivityBookingFactory;
import kz.aitu.sdp.finalproject.factory.BookingItem;
import kz.aitu.sdp.finalproject.factory.FlightBookingFactory;
import kz.aitu.sdp.finalproject.factory.HotelBookingFactory;
import kz.aitu.sdp.finalproject.model.*;
import kz.aitu.sdp.finalproject.observer.*;
import kz.aitu.sdp.finalproject.strategy.FastestRouteStrategy;
import kz.aitu.sdp.finalproject.strategy.OptimizedPlan;
import kz.aitu.sdp.finalproject.strategy.RouteOptimizerContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Comprehensive Team Integration Test: All 6 Coordinated Patterns")
class IntegrationEndToEndTest {

    @Test
    @DisplayName("Test 22: Full end-to-end trip planning workflow exercising all 6 patterns cohesively")
    void testEndToEndCohesiveWorkflowAllSixPatterns() {
        // 1. Adapter (Student 2): Search external flights
        FlightSearchProvider flightProvider = new AmadeusFlightAdapter(new LegacyAmadeusFlightApi());
        List<FlightOption> flights = flightProvider.searchAvailableFlights("ALA", "DXB", LocalDate.now().plusMonths(1));
        assertFalse(flights.isEmpty());
        FlightOption selectedFlightOption = flights.get(0);

        // 2. Factory Method (Student 1): Create items
        BookingItem flight = new FlightBookingFactory().createCustomFlight(
                "B-1", selectedFlightOption.flightNumber(), selectedFlightOption.airline(), "Economy", selectedFlightOption.priceUsd());
        BookingItem hotel = new HotelBookingFactory().createBooking(
                "B-2", "Atlantis The Palm", "Ocean Suite", 300.0, 3);
        BookingItem excursion = new ActivityBookingFactory().createBooking(
                "B-3", "Desert Safari BBQ Tour", "Dubai", 75.0, 5.0);

        List<BookingItem> candidateItems = List.of(flight, hotel, excursion);

        // 3. Strategy (Student 2): Optimize route
        RouteOptimizerContext strategyContext = new RouteOptimizerContext(new FastestRouteStrategy());
        Location from = new Location("Almaty", "Kazakhstan", "ALA");
        Location to = new Location("Dubai", "UAE", "DXB");
        OptimizedPlan plan = strategyContext.executeOptimization(from, to, candidateItems, 2500.0);
        assertNotNull(plan);

        // 4. Builder (Student 1): Assemble itinerary
        Traveler traveler = new Traveler("Mukhamedzhan Tileuke", "mukhamedzhan@aitu.kz", "+7-707-111-2233", "N9998881");
        TripItinerary itinerary = new TripItineraryBuilder()
                .setId("ITIN-DUBAI-01")
                .setTripName("Dubai Luxury Getaway")
                .setTraveler(traveler)
                .setOrigin(from)
                .setDestination(to)
                .setDateRange(LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(1).plusDays(3))
                .setBudgetLimit(2500.0)
                .addBookings(plan.selectedBookings())
                .build();

        assertEquals(3, itinerary.getDurationDays());
        assertTrue(itinerary.isWithinBudget());

        // 5. Observer (Student 2): Setup notifications
        TripNotificationSubject notificationHub = new TripNotificationSubject("TravelHub");
        EmailNotificationListener emailNotifier = new EmailNotificationListener("EMAIL", traveler.email());
        SmsNotificationListener smsNotifier = new SmsNotificationListener("SMS", traveler.phoneNumber());

        notificationHub.subscribe(TripEventType.BOOKING_CONFIRMED, emailNotifier);
        notificationHub.subscribe(TripEventType.BOOKING_CONFIRMED, smsNotifier);

        // 6. Facade (Student 1): One-click checkout
        TravelBookingFacade facade = new TravelBookingFacade(
                new FlightSubsystem(),
                new HotelSubsystem(),
                new PaymentSubsystem(),
                new InsuranceSubsystem()
        );

        PaymentDetails payment = new PaymentDetails(traveler.name(), "5500123456789999", "888", "11/28");
        BookingResult result = facade.bookEntireTrip(itinerary, payment);

        assertTrue(result.success());

        // Publish event to trigger Observer listeners
        notificationHub.notifyObservers(TripEvent.of(
                "EVT-CONFIRM", TripEventType.BOOKING_CONFIRMED, itinerary.getId(),
                "Checkout successful! Reference: " + result.confirmationCode()
        ));

        // Verify listeners received event
        assertEquals(1, emailNotifier.getReceivedEvents().size());
        assertEquals(1, smsNotifier.getReceivedEvents().size());
        assertTrue(emailNotifier.getReceivedEvents().get(0).message().contains(result.confirmationCode()));
    }
}
