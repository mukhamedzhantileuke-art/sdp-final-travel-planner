package kz.aitu.sdp.finalproject.adapter;

import kz.aitu.sdp.finalproject.adapter.legacy.AmadeusRawFlightData;
import kz.aitu.sdp.finalproject.adapter.legacy.LegacyAmadeusFlightApi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Adapter Pattern implementation (Student 2).
 *
 * Implements the modern {@link FlightSearchProvider} target interface by wrapping
 * and adapting the incompatible {@link LegacyAmadeusFlightApi} adaptee.
 *
 * Converts legacy fare integers (cents -> USD), parses raw ISO timestamps,
 * and decodes carrier IATA codes into human-readable airline names.
 */
public class AmadeusFlightAdapter implements FlightSearchProvider {

    private final LegacyAmadeusFlightApi legacyApi;

    private static final Map<String, String> CARRIER_NAMES = Map.of(
            "KC", "Air Astana",
            "LH", "Lufthansa",
            "TK", "Turkish Airlines",
            "AF", "Air France",
            "BA", "British Airways"
    );

    public AmadeusFlightAdapter(LegacyAmadeusFlightApi legacyApi) {
        this.legacyApi = Objects.requireNonNull(legacyApi, "legacyApi cannot be null");
    }

    @Override
    public List<FlightOption> searchAvailableFlights(String originAirport, String destinationAirport, LocalDate departureDate) {
        Objects.requireNonNull(originAirport, "originAirport cannot be null");
        Objects.requireNonNull(destinationAirport, "destinationAirport cannot be null");
        Objects.requireNonNull(departureDate, "departureDate cannot be null");

        String formattedDate = departureDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        // Delegate to adaptee's incompatible method signature
        List<AmadeusRawFlightData> rawData = legacyApi.lookupSchedulesRawXml(originAirport, destinationAirport, formattedDate);

        List<FlightOption> cleanOptions = new ArrayList<>();
        for (AmadeusRawFlightData raw : rawData) {
            String airlineName = CARRIER_NAMES.getOrDefault(raw.getCarrierCode(), raw.getCarrierCode() + " Airlines");
            String flightNumber = raw.getCarrierCode() + "-" + raw.getFlightNumeric();

            LocalDateTime dep = LocalDateTime.parse(raw.getDepartureIsoTimestamp(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime arr = LocalDateTime.parse(raw.getArrivalIsoTimestamp(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            double priceUsd = raw.getFareInCents() / 100.0;

            cleanOptions.add(new FlightOption(
                    flightNumber,
                    airlineName,
                    originAirport,
                    destinationAirport,
                    dep,
                    arr,
                    priceUsd,
                    raw.getFlightTimeMinutes()
            ));
        }

        return cleanOptions;
    }
}
