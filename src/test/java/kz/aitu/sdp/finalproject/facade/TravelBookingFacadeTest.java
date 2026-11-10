package kz.aitu.sdp.finalproject.facade;

import kz.aitu.sdp.finalproject.builder.TripItineraryBuilder;
import kz.aitu.sdp.finalproject.facade.subsystems.FlightSubsystem;
import kz.aitu.sdp.finalproject.facade.subsystems.HotelSubsystem;
import kz.aitu.sdp.finalproject.facade.subsystems.InsuranceSubsystem;
import kz.aitu.sdp.finalproject.facade.subsystems.PaymentSubsystem;
import kz.aitu.sdp.finalproject.factory.FlightBookingItem;
import kz.aitu.sdp.finalproject.factory.HotelBookingItem;
import kz.aitu.sdp.finalproject.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pattern 3 Tests: Facade Pattern (Student 1)")
class TravelBookingFacadeTest {

    private TravelBookingFacade facade;
    private FlightSubsystem flightSubsystem;
    private HotelSubsystem hotelSubsystem;
    private PaymentSubsystem paymentSubsystem;
    private InsuranceSubsystem insuranceSubsystem;
    private TripItinerary itinerary;
    private PaymentDetails validPayment;

    @BeforeEach
    void setUp() {
        flightSubsystem = new FlightSubsystem();
        hotelSubsystem = new HotelSubsystem();
        paymentSubsystem = new PaymentSubsystem();
        insuranceSubsystem = new InsuranceSubsystem();

        facade = new TravelBookingFacade(flightSubsystem, hotelSubsystem, paymentSubsystem, insuranceSubsystem);

        Traveler traveler = new Traveler("Bob Brown", "bob@travel.com", "+1-202-555-0144", "K8882221");
        Location from = new Location("London", "UK", "LHR");
        Location to = new Location("Barcelona", "Spain", "BCN");

        itinerary = new TripItineraryBuilder()
                .setId("ITIN-FACADE-01")
                .setTripName("Barcelona Weekend")
                .setTraveler(traveler)
                .setOrigin(from)
                .setDestination(to)
                .setDateRange(LocalDate.of(2027, 6, 1), LocalDate.of(2027, 6, 5))
                .setBudgetLimit(1000.0)
                .addBooking(new FlightBookingItem("F1", "BA-402", "British Airways", "Economy", 220.0))
                .addBooking(new HotelBookingItem("H1", "Catalonia Plaza", "Standard", 4, 90.0))
                .build();

        validPayment = new PaymentDetails("Bob Brown", "4000123456789010", "123", "08/29");
    }

    @Test
    @DisplayName("Test 9: Facade coordinates all subsystems into one successful checkout")
    void testSuccessfulOneClickBooking() {
        BookingResult result = facade.bookEntireTrip(itinerary, validPayment);

        assertTrue(result.success());
        assertEquals("ITIN-FACADE-01", result.itineraryId());
        assertTrue(result.confirmationCode().startsWith("CONF-"));
        assertEquals(580.0, result.totalAmountCharged(), 0.001); // 220 + (4 * 90) = 580

        // Subsystems should have confirmed reservations
        assertEquals(1, flightSubsystem.getConfirmedReservations().size());
        assertEquals(1, hotelSubsystem.getConfirmedBookings().size());

        // Bookings in itinerary must be marked confirmed
        assertTrue(itinerary.getBookings().get(0).isConfirmed());
        assertTrue(itinerary.getBookings().get(1).isConfirmed());
    }

    @Test
    @DisplayName("Test 10: Facade handles payment decline and aborts reservations")
    void testPaymentDeclinedRollback() {
        PaymentDetails invalidPayment = new PaymentDetails("Bob Brown", "123", "9", "01/20"); // Invalid card
        BookingResult result = facade.bookEntireTrip(itinerary, invalidPayment);

        assertFalse(result.success());
        assertEquals("NONE", result.confirmationCode());
        assertEquals(0, flightSubsystem.getConfirmedReservations().size());
        assertEquals(0, hotelSubsystem.getConfirmedBookings().size());
    }

    @Test
    @DisplayName("Test 11: Facade successfully cancels all bookings across subsystems")
    void testEntireTripCancellation() {
        facade.bookEntireTrip(itinerary, validPayment);
        assertEquals(1, flightSubsystem.getConfirmedReservations().size());

        boolean cancelled = facade.cancelEntireTrip(itinerary, validPayment);
        assertTrue(cancelled);
        assertEquals(0, flightSubsystem.getConfirmedReservations().size());
        assertEquals(0, hotelSubsystem.getConfirmedBookings().size());
        assertFalse(itinerary.getBookings().get(0).isConfirmed());
    }
}
