package kz.aitu.sdp.finalproject.facade.subsystems;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Complex subsystem 1: Coordinates airline seat reservations with air carriers.
 */
public class FlightSubsystem {

    private final Set<String> confirmedReservations = new HashSet<>();

    public boolean reserveFlight(String flightNumber, String passengerName, String passport) {
        if (flightNumber == null || passengerName == null || passport == null) {
            return false;
        }
        String key = flightNumber + ":" + passengerName;
        confirmedReservations.add(key);
        System.out.printf("  [FlightSubsystem] Reserved seat on flight %s for %s (Passport: %s)\n",
                flightNumber, passengerName, passport);
        return true;
    }

    public boolean cancelFlight(String flightNumber, String passengerName) {
        String key = flightNumber + ":" + passengerName;
        boolean removed = confirmedReservations.remove(key);
        if (removed) {
            System.out.printf("  [FlightSubsystem] Cancelled seat reservation on %s for %s\n", flightNumber, passengerName);
        }
        return removed;
    }

    public Set<String> getConfirmedReservations() {
        return Collections.unmodifiableSet(confirmedReservations);
    }
}
