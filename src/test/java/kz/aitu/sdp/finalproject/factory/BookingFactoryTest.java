package kz.aitu.sdp.finalproject.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pattern 2 Tests: Factory Method Pattern (Student 1)")
class BookingFactoryTest {

    @Test
    @DisplayName("Test 5: FlightBookingFactory instantiates valid FlightBookingItem")
    void testFlightBookingFactory() {
        BookingFactory factory = new FlightBookingFactory();
        BookingItem item = factory.createBooking("BK-FL-1", "AF-114", "Air France", 540.0, 0);

        assertNotNull(item);
        assertEquals(BookingType.FLIGHT, item.getType());
        assertEquals("Flight AF-114 (Air France)", item.getTitle());
        assertEquals(540.0, item.getCost());
        assertFalse(item.isConfirmed());

        item.confirm();
        assertTrue(item.isConfirmed());
    }

    @Test
    @DisplayName("Test 6: HotelBookingFactory computes total cost correctly from rate and nights")
    void testHotelBookingFactory() {
        BookingFactory factory = new HotelBookingFactory();
        BookingItem hotel = factory.createBooking("BK-HT-1", "Grand Palace", "Suite", 200.0, 4); // 4 nights @ $200

        assertNotNull(hotel);
        assertEquals(BookingType.HOTEL, hotel.getType());
        assertEquals(800.0, hotel.getCost(), 0.001);
        assertTrue(hotel.getDetails().contains("Nights: 4"));
    }

    @Test
    @DisplayName("Test 7: ActivityBookingFactory creates excursion item with correct duration")
    void testActivityBookingFactory() {
        BookingFactory factory = new ActivityBookingFactory();
        BookingItem excursion = factory.createBooking("BK-ACT-1", "Colosseum Guided Tour", "Rome", 45.0, 3.5);

        assertNotNull(excursion);
        assertEquals(BookingType.ACTIVITY, excursion.getType());
        assertEquals(45.0, excursion.getCost());
        assertTrue(excursion.getDetails().contains("3.5h"));
    }

    @Test
    @DisplayName("Test 8: Polymorphic booking lifecycle works seamlessly across all items")
    void testPolymorphicLifecycle() {
        BookingItem flight = new FlightBookingFactory().createBooking("1", "A", "B", 100, 0);
        BookingItem hotel = new HotelBookingFactory().createBooking("2", "H", "R", 50, 2);

        assertFalse(flight.isConfirmed());
        assertFalse(hotel.isConfirmed());

        flight.confirm();
        hotel.confirm();

        assertTrue(flight.isConfirmed());
        assertTrue(hotel.isConfirmed());

        flight.cancel();
        assertFalse(flight.isConfirmed());
    }
}
