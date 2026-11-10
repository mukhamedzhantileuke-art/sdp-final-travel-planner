package kz.aitu.sdp.finalproject.adapter.legacy;

/**
 * Legacy incompatible data structure from 3rd-party Amadeus GDS partner API.
 * Uses integer cents, legacy carrier codes, and concatenated route strings.
 */
public class AmadeusRawFlightData {

    private final String carrierCode;
    private final int flightNumeric;
    private final String routeSegment; // e.g. "ALA-CDG"
    private final String departureIsoTimestamp;
    private final String arrivalIsoTimestamp;
    private final long fareInCents; // e.g. 45000 cents = $450.00
    private final int flightTimeMinutes;

    public AmadeusRawFlightData(String carrierCode,
                                int flightNumeric,
                                String routeSegment,
                                String departureIsoTimestamp,
                                String arrivalIsoTimestamp,
                                long fareInCents,
                                int flightTimeMinutes) {
        this.carrierCode = carrierCode;
        this.flightNumeric = flightNumeric;
        this.routeSegment = routeSegment;
        this.departureIsoTimestamp = departureIsoTimestamp;
        this.arrivalIsoTimestamp = arrivalIsoTimestamp;
        this.fareInCents = fareInCents;
        this.flightTimeMinutes = flightTimeMinutes;
    }

    public String getCarrierCode() {
        return carrierCode;
    }

    public int getFlightNumeric() {
        return flightNumeric;
    }

    public String getRouteSegment() {
        return routeSegment;
    }

    public String getDepartureIsoTimestamp() {
        return departureIsoTimestamp;
    }

    public String getArrivalIsoTimestamp() {
        return arrivalIsoTimestamp;
    }

    public long getFareInCents() {
        return fareInCents;
    }

    public int getFlightTimeMinutes() {
        return flightTimeMinutes;
    }
}
