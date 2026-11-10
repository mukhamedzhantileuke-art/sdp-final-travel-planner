package kz.aitu.sdp.finalproject.facade;

import kz.aitu.sdp.finalproject.facade.subsystems.FlightSubsystem;
import kz.aitu.sdp.finalproject.facade.subsystems.HotelSubsystem;
import kz.aitu.sdp.finalproject.facade.subsystems.InsuranceSubsystem;
import kz.aitu.sdp.finalproject.facade.subsystems.PaymentSubsystem;
import kz.aitu.sdp.finalproject.factory.ActivityBookingItem;
import kz.aitu.sdp.finalproject.factory.BookingItem;
import kz.aitu.sdp.finalproject.factory.FlightBookingItem;
import kz.aitu.sdp.finalproject.factory.HotelBookingItem;
import kz.aitu.sdp.finalproject.model.BookingResult;
import kz.aitu.sdp.finalproject.model.PaymentDetails;
import kz.aitu.sdp.finalproject.model.TripItinerary;
import kz.aitu.sdp.finalproject.model.TripStatus;

import java.util.Objects;
import java.util.UUID;

/**
 * Facade Pattern implementation (Student 1).
 *
 * Encapsulates the complexity of interacting with disparate subsystems:
 * flights, lodging, payment processing, and traveler insurance underwriting.
 *
 * Adheres to the Principle of Least Knowledge (Law of Demeter) by providing
 * a unified, high-level checkout interface.
 */
public class TravelBookingFacade {

    private final FlightSubsystem flightSubsystem;
    private final HotelSubsystem hotelSubsystem;
    private final PaymentSubsystem paymentSubsystem;
    private final InsuranceSubsystem insuranceSubsystem;

    public TravelBookingFacade(FlightSubsystem flightSubsystem,
                               HotelSubsystem hotelSubsystem,
                               PaymentSubsystem paymentSubsystem,
                               InsuranceSubsystem insuranceSubsystem) {
        this.flightSubsystem = Objects.requireNonNull(flightSubsystem, "flightSubsystem cannot be null");
        this.hotelSubsystem = Objects.requireNonNull(hotelSubsystem, "hotelSubsystem cannot be null");
        this.paymentSubsystem = Objects.requireNonNull(paymentSubsystem, "paymentSubsystem cannot be null");
        this.insuranceSubsystem = Objects.requireNonNull(insuranceSubsystem, "insuranceSubsystem cannot be null");
    }

    /**
     * Executes end-to-end checkout for an assembled itinerary in one unified call.
     */
    public BookingResult bookEntireTrip(TripItinerary itinerary, PaymentDetails payment) {
        Objects.requireNonNull(itinerary, "itinerary cannot be null");
        Objects.requireNonNull(payment, "payment cannot be null");

        double totalAmount = itinerary.getTotalCost();
        System.out.printf("\n[TravelBookingFacade] Commencing checkout for trip '%s' (Total: $%.2f)...\n",
                itinerary.getTripName(), totalAmount);

        // 1. Process financial transaction
        boolean paymentSuccess = paymentSubsystem.processPayment(payment, totalAmount);
        if (!paymentSuccess) {
            return BookingResult.failure(itinerary.getId(), "Payment authorization declined by merchant bank.");
        }

        // 2. Coordinate reservations across all booking items
        for (BookingItem item : itinerary.getBookings()) {
            if (item instanceof FlightBookingItem flight) {
                boolean reserved = flightSubsystem.reserveFlight(
                        flight.getFlightNumber(),
                        itinerary.getTraveler().name(),
                        itinerary.getTraveler().passportNumber()
                );
                if (reserved) {
                    flight.confirm();
                }
            } else if (item instanceof HotelBookingItem hotel) {
                boolean reserved = hotelSubsystem.reserveRoom(
                        hotel.getHotelName(),
                        itinerary.getTraveler().name(),
                        hotel.getNumberOfNights()
                );
                if (reserved) {
                    hotel.confirm();
                }
            } else if (item instanceof ActivityBookingItem activity) {
                System.out.printf("  [ActivitySubsystem] Confirmed pass for '%s'\n", activity.getTitle());
                activity.confirm();
            }
        }

        // 3. Issue travel insurance policy
        String policyId = insuranceSubsystem.issuePolicy(itinerary.getTraveler(), itinerary.getDurationDays());

        // 4. Generate confirmation code
        String confirmationCode = "CONF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        itinerary.withStatus(TripStatus.CONFIRMED);

        System.out.printf("[TravelBookingFacade] Successfully completed checkout! Confirmation: %s (Policy: %s)\n",
                confirmationCode, policyId);

        return BookingResult.success(
                itinerary.getId(),
                confirmationCode,
                totalAmount,
                String.format("Trip '%s' successfully booked and confirmed. Policy: %s", itinerary.getTripName(), policyId)
        );
    }

    /**
     * Cancels all reservations and processes financial refunds.
     */
    public boolean cancelEntireTrip(TripItinerary itinerary, PaymentDetails payment) {
        Objects.requireNonNull(itinerary, "itinerary cannot be null");
        System.out.printf("\n[TravelBookingFacade] Initiating cancellation for trip '%s'...\n", itinerary.getTripName());

        for (BookingItem item : itinerary.getBookings()) {
            if (item instanceof FlightBookingItem flight) {
                flightSubsystem.cancelFlight(flight.getFlightNumber(), itinerary.getTraveler().name());
                flight.cancel();
            } else if (item instanceof HotelBookingItem hotel) {
                hotelSubsystem.cancelRoom(hotel.getHotelName(), itinerary.getTraveler().name());
                hotel.cancel();
            } else if (item instanceof ActivityBookingItem activity) {
                activity.cancel();
            }
        }

        if (payment != null) {
            paymentSubsystem.refundPayment(payment, itinerary.getTotalCost());
        }

        itinerary.withStatus(TripStatus.CANCELLED);
        System.out.println("[TravelBookingFacade] Trip successfully cancelled and refunded.");
        return true;
    }
}
