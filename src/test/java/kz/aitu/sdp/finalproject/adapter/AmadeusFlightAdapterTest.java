package kz.aitu.sdp.finalproject.adapter;

import kz.aitu.sdp.finalproject.adapter.legacy.LegacyAmadeusFlightApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pattern 4 Tests: Adapter Pattern (Student 2)")
class AmadeusFlightAdapterTest {

    private FlightSearchProvider adapter;

    @BeforeEach
    void setUp() {
        LegacyAmadeusFlightApi legacyApi = new LegacyAmadeusFlightApi();
        adapter = new AmadeusFlightAdapter(legacyApi);
    }

    @Test
    @DisplayName("Test 12: Adapter converts legacy XML response to clean FlightOption domain objects")
    void testAdapterConvertsLegacyPayload() {
        LocalDate travelDate = LocalDate.of(2027, 7, 15);
        List<FlightOption> options = adapter.searchAvailableFlights("ALA", "FRA", travelDate);

        assertNotNull(options);
        assertFalse(options.isEmpty());
        assertEquals(3, options.size());

        FlightOption first = options.get(0);
        assertEquals("ALA", first.originAirport());
        assertEquals("FRA", first.destinationAirport());
        assertEquals("KC-911", first.flightNumber());
        assertEquals("Air Astana", first.airline());
        assertEquals(480.0, first.priceUsd(), 0.001); // 48000 cents -> $480.00
        assertEquals(495, first.durationMinutes());
    }

    @Test
    @DisplayName("Test 13: Adapter correctly converts integer cents to double USD")
    void testFareConversionAccuracy() {
        LocalDate travelDate = LocalDate.of(2027, 7, 15);
        List<FlightOption> options = adapter.searchAvailableFlights("ALA", "FRA", travelDate);

        // LH-647: 62000 cents -> 620.00 USD
        FlightOption second = options.get(1);
        assertEquals(620.0, second.priceUsd(), 0.001);

        // TK-351: 39000 cents -> 390.00 USD
        FlightOption third = options.get(2);
        assertEquals(390.0, third.priceUsd(), 0.001);
    }

    @Test
    @DisplayName("Test 14: Adapter correctly decodes carrier codes to airline names")
    void testCarrierCodeTranslation() {
        LocalDate travelDate = LocalDate.of(2027, 7, 15);
        List<FlightOption> options = adapter.searchAvailableFlights("ALA", "FRA", travelDate);

        assertTrue(options.stream().anyMatch(f -> f.airline().equals("Air Astana")));
        assertTrue(options.stream().anyMatch(f -> f.airline().equals("Lufthansa")));
        assertTrue(options.stream().anyMatch(f -> f.airline().equals("Turkish Airlines")));
    }
}
