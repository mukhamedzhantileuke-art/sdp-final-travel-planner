package kz.aitu.sdp.finalproject.facade.subsystems;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Complex subsystem 2: Coordinates lodging availability and room locking with hotels.
 */
public class HotelSubsystem {

    private final Set<String> confirmedBookings = new HashSet<>();

    public boolean reserveRoom(String hotelName, String guestName, int nights) {
        if (hotelName == null || guestName == null || nights <= 0) {
            return false;
        }
        String key = hotelName + ":" + guestName;
        confirmedBookings.add(key);
        System.out.printf("  [HotelSubsystem] Booked %d night(s) at '%s' for guest %s\n", nights, hotelName, guestName);
        return true;
    }

    public boolean cancelRoom(String hotelName, String guestName) {
        String key = hotelName + ":" + guestName;
        boolean removed = confirmedBookings.remove(key);
        if (removed) {
            System.out.printf("  [HotelSubsystem] Cancelled booking at '%s' for guest %s\n", hotelName, guestName);
        }
        return removed;
    }

    public Set<String> getConfirmedBookings() {
        return Collections.unmodifiableSet(confirmedBookings);
    }
}
