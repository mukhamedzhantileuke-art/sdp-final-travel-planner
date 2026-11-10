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
import kz.aitu.sdp.finalproject.factory.*;
import kz.aitu.sdp.finalproject.model.*;
import kz.aitu.sdp.finalproject.observer.*;
import kz.aitu.sdp.finalproject.strategy.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Composition Root & Demonstration Runner.
 *
 * Demonstrates all 6 design patterns functioning cohesively in a unified travel booking system:
 *
 * Student 1:
 *  - Pattern 1: Builder (TripItineraryBuilder)
 *  - Pattern 2: Factory Method (Flight, Hotel, Activity Booking Creators)
 *  - Pattern 3: Facade (TravelBookingFacade)
 *
 * Student 2:
 *  - Pattern 4: Adapter (AmadeusFlightAdapter wrapping legacy GDS)
 *  - Pattern 5: Strategy (Fastest, Cheapest, Scenic Route Optimizers)
 *  - Pattern 6: Observer (Dynamic multi-channel notification engine)
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("          TRAVEL ITINERARY PLANNER - FINAL PROJECT DEMONSTRATION               ");
        System.out.println("  Course: Software Design Patterns (AITU SE-2531) | 6 Cohesive Patterns       ");
        System.out.println("  Team: Student 1 (Builder, Factory, Facade) & Student 2 (Adapter, Strategy, Observer)");
        System.out.println("===============================================================================\n");

        // ------------------------------------------------------------------------------------
        // PHASE 1: EXTERNAL INTEGRATION VIA ADAPTER PATTERN (Student 2)
        // ------------------------------------------------------------------------------------
        System.out.println(">>> [1/6] ADAPTER PATTERN (Student 2): Querying Flights via Legacy Amadeus GDS");
        LegacyAmadeusFlightApi legacyAmadeusApi = new LegacyAmadeusFlightApi();
        FlightSearchProvider flightProvider = new AmadeusFlightAdapter(legacyAmadeusApi);

        LocalDate departureDate = LocalDate.now().plusWeeks(2);
        List<FlightOption> availableFlights = flightProvider.searchAvailableFlights("ALA", "CDG", departureDate);
        System.out.println("  -> Clean domain flights adapted from legacy XML payload:");
        for (FlightOption flight : availableFlights) {
            System.out.printf("     * %s | Cost: $%.2f | Duration: %d mins\n",
                    flight.flightNumber(), flight.priceUsd(), flight.durationMinutes());
        }

        // ------------------------------------------------------------------------------------
        // PHASE 2: CREATING BOOKINGS VIA FACTORY METHOD PATTERN (Student 1)
        // ------------------------------------------------------------------------------------
        System.out.println("\n>>> [2/6] FACTORY METHOD PATTERN (Student 1): Instantiating Travel Items");
        FlightBookingFactory flightFactory = new FlightBookingFactory();
        HotelBookingFactory hotelFactory = new HotelBookingFactory();
        ActivityBookingFactory activityFactory = new ActivityBookingFactory();

        FlightOption bestFlight = availableFlights.get(0);
        BookingItem flightItem = flightFactory.createCustomFlight(
                "BKG-FLIGHT-01", bestFlight.flightNumber(), bestFlight.airline(), "Economy Plus", bestFlight.priceUsd());

        BookingItem hotelItem = hotelFactory.createBooking(
                "BKG-HOTEL-01", "Hôtel Le Marais Paris", "Deluxe Double", 110.0, 5);

        BookingItem activityLouvre = activityFactory.createBooking(
                "BKG-ACT-01", "Louvre Museum & Seine River Cruise", "Paris", 65.0, 4.0);

        BookingItem activityVersailles = activityFactory.createBooking(
                "BKG-ACT-02", "Palace of Versailles Guided Day Tour", "Versailles", 85.0, 6.0);

        List<BookingItem> candidateBookings = List.of(flightItem, hotelItem, activityLouvre, activityVersailles);
        System.out.println("  -> Factory created booking items:");
        for (BookingItem item : candidateBookings) {
            System.out.printf("     * [%s] %s (Cost: $%.2f)\n", item.getType(), item.getTitle(), item.getCost());
        }

        // ------------------------------------------------------------------------------------
        // PHASE 3: ALGORITHM SELECTION VIA STRATEGY PATTERN (Student 2)
        // ------------------------------------------------------------------------------------
        System.out.println("\n>>> [3/6] STRATEGY PATTERN (Student 2): Optimizing Route & Budget");
        Location origin = new Location("Almaty", "Kazakhstan", "ALA");
        Location destination = new Location("Paris", "France", "CDG");
        double userBudget = 1500.0;

        RouteOptimizerContext routeContext = new RouteOptimizerContext(new FastestRouteStrategy());
        OptimizedPlan fastestPlan = routeContext.executeOptimization(origin, destination, candidateBookings, userBudget);
        System.out.printf("  * Strategy 1: %s -> Total: $%.2f | Duration: %.1fh\n",
                fastestPlan.strategyName(), fastestPlan.totalCost(), fastestPlan.estimatedTravelDurationHours());

        // Runtime Strategy Switch to Cheapest
        routeContext.setStrategy(new CheapestRouteStrategy());
        OptimizedPlan cheapestPlan = routeContext.executeOptimization(origin, destination, candidateBookings, userBudget);
        System.out.printf("  * Strategy 2: %s -> Total: $%.2f | Duration: %.1fh\n",
                cheapestPlan.strategyName(), cheapestPlan.totalCost(), cheapestPlan.estimatedTravelDurationHours());

        // Runtime Strategy Switch to Scenic
        routeContext.setStrategy(new ScenicRouteStrategy());
        OptimizedPlan scenicPlan = routeContext.executeOptimization(origin, destination, candidateBookings, userBudget);
        System.out.printf("  * Strategy 3: %s -> Total: $%.2f | Duration: %.1fh\n",
                scenicPlan.strategyName(), scenicPlan.totalCost(), scenicPlan.estimatedTravelDurationHours());

        // Choose Scenic plan for rich travel experience
        OptimizedPlan chosenPlan = scenicPlan;
        System.out.printf("  -> Selected: '%s' (Rationale: %s)\n", chosenPlan.strategyName(), chosenPlan.rationale());

        // ------------------------------------------------------------------------------------
        // PHASE 4: OBJECT ASSEMBLY VIA BUILDER PATTERN (Student 1)
        // ------------------------------------------------------------------------------------
        System.out.println("\n>>> [4/6] BUILDER PATTERN (Student 1): Stepwise Itinerary Assembly");
        Traveler traveler = new Traveler("Mukhamedzhan Tileuke", "mukhamedzhan@aitu.kz", "+7-707-555-0199", "N12345678");

        TripItinerary itinerary = new TripItineraryBuilder()
                .setId("ITIN-PARIS-2026")
                .setTripName("Paris Architectural & Cultural Discovery")
                .setTraveler(traveler)
                .setOrigin(origin)
                .setDestination(destination)
                .setDateRange(departureDate, departureDate.plusDays(6))
                .setBudgetLimit(userBudget)
                .addBookings(chosenPlan.selectedBookings())
                .addNote("Window seat requested on Air Astana segment")
                .addNote("Late check-in confirmed with hotel reception")
                .build();

        System.out.printf("  -> Assembled Itinerary: %s\n", itinerary.getTripName());
        System.out.printf("     Traveler: %s | Route: %s -> %s\n", itinerary.getTraveler().name(), itinerary.getOrigin().city(), itinerary.getDestination().city());
        System.out.printf("     Duration: %d days | Total Estimated: $%.2f | Within Budget: %b\n",
                itinerary.getDurationDays(), itinerary.getTotalCost(), itinerary.isWithinBudget());

        // ------------------------------------------------------------------------------------
        // PHASE 5: NOTIFICATION SYSTEM VIA OBSERVER PATTERN (Student 2)
        // ------------------------------------------------------------------------------------
        System.out.println("\n>>> [5/6] OBSERVER PATTERN (Student 2): Registering Notification Channels");
        TripNotificationSubject notificationSubject = new TripNotificationSubject("TravelerNotificationHub");

        EmailNotificationListener emailNotifier = new EmailNotificationListener("EMAIL-SVC", traveler.email());
        SmsNotificationListener smsNotifier = new SmsNotificationListener("SMS-GW", traveler.phoneNumber());
        MobilePushNotificationListener pushNotifier = new MobilePushNotificationListener("PUSH-SVC", "DEV-TOKEN-IPHONE-15");
        PriceDropAlertTrackerListener priceTracker = new PriceDropAlertTrackerListener("PRICE-TRACKER");

        notificationSubject.subscribe(TripEventType.ITINERARY_CREATED, emailNotifier);
        notificationSubject.subscribe(TripEventType.ITINERARY_CREATED, pushNotifier);
        notificationSubject.subscribe(TripEventType.BOOKING_CONFIRMED, emailNotifier);
        notificationSubject.subscribe(TripEventType.BOOKING_CONFIRMED, smsNotifier);
        notificationSubject.subscribe(TripEventType.BOOKING_CONFIRMED, pushNotifier);
        notificationSubject.subscribe(TripEventType.FLIGHT_DELAYED, smsNotifier);
        notificationSubject.subscribe(TripEventType.FLIGHT_DELAYED, pushNotifier);
        notificationSubject.subscribe(TripEventType.PRICE_DROP_ALERT, priceTracker);

        // Notify that itinerary was successfully prepared
        notificationSubject.notifyObservers(TripEvent.of(
                "EVT-001", TripEventType.ITINERARY_CREATED, itinerary.getId(),
                "Your draft itinerary '" + itinerary.getTripName() + "' has been generated and is awaiting payment."
        ));

        // ------------------------------------------------------------------------------------
        // PHASE 6: ONE-CLICK CHECKOUT VIA FACADE PATTERN (Student 1)
        // ------------------------------------------------------------------------------------
        System.out.println("\n>>> [6/6] FACADE PATTERN (Student 1): One-Click Checkout Subsystem Orchestration");
        TravelBookingFacade bookingFacade = new TravelBookingFacade(
                new FlightSubsystem(),
                new HotelSubsystem(),
                new PaymentSubsystem(),
                new InsuranceSubsystem()
        );

        PaymentDetails payment = new PaymentDetails(
                traveler.name(), "4400 1234 5678 9876", "777", "12/28"
        );

        BookingResult bookingResult = bookingFacade.bookEntireTrip(itinerary, payment);
        if (bookingResult.success()) {
            notificationSubject.notifyObservers(TripEvent.of(
                    "EVT-002", TripEventType.BOOKING_CONFIRMED, itinerary.getId(),
                    String.format("Payment of $%.2f confirmed! Booking reference: %s. Documents sent.",
                            bookingResult.totalAmountCharged(), bookingResult.confirmationCode())
            ));
        }

        // ------------------------------------------------------------------------------------
        // SIMULATION: REAL-TIME TRAVEL UPDATES (OBSERVER IN ACTION)
        // ------------------------------------------------------------------------------------
        System.out.println("\n>>> REAL-TIME EVENT SIMULATION (Observer Reaction to Operational Events):");
        notificationSubject.notifyObservers(TripEvent.of(
                "EVT-003", TripEventType.FLIGHT_DELAYED, itinerary.getId(),
                "Flight KC-911 delayed by 35 mins due to air traffic control in Frankfurt. New departure: 07:05."
        ));

        notificationSubject.notifyObservers(TripEvent.of(
                "EVT-004", TripEventType.PRICE_DROP_ALERT, itinerary.getId(),
                "Price drop detected! Louvre Museum tour tickets discounted by $15.00 for your dates."
        ));

        System.out.println("\n===============================================================================");
        System.out.println("                 ALL 6 DESIGN PATTERNS VERIFIED & OPERATIONAL!                 ");
        System.out.println("===============================================================================");
    }
}
