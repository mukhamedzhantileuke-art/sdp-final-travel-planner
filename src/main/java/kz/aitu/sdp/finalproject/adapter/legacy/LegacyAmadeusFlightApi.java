package kz.aitu.sdp.finalproject.adapter.legacy;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptee in the Adapter Pattern (Student 2).
 * Simulates a legacy 3rd-party Global Distribution System (GDS) with an
 * incompatible API contract, legacy method signatures, and raw data representations.
 */
public class LegacyAmadeusFlightApi {

    public List<AmadeusRawFlightData> lookupSchedulesRawXml(String originIata, String destIata, String yyyyMmDd) {
        System.out.printf("  [LegacyAmadeusFlightApi] Executing legacy GDS search query: %s -> %s on %s\n",
                originIata, destIata, yyyyMmDd);

        List<AmadeusRawFlightData> rawList = new ArrayList<>();

        // Simulated legacy flight entries
        rawList.add(new AmadeusRawFlightData(
                "KC",
                911,
                originIata + "-" + destIata,
                yyyyMmDd + "T06:30:00",
                yyyyMmDd + "T14:45:00",
                48000L, // $480.00
                495
        ));

        rawList.add(new AmadeusRawFlightData(
                "LH",
                647,
                originIata + "-" + destIata,
                yyyyMmDd + "T11:15:00",
                yyyyMmDd + "T18:20:00",
                62000L, // $620.00
                425
        ));

        rawList.add(new AmadeusRawFlightData(
                "TK",
                351,
                originIata + "-" + destIata,
                yyyyMmDd + "T19:00:00",
                yyyyMmDd + "T04:30:00",
                39000L, // $390.00
                570
        ));

        return rawList;
    }
}
