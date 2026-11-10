package kz.aitu.sdp.finalproject.observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Concrete Observer 4 (Student 2): Analytical listener monitoring price drop events.
 */
public class PriceDropAlertTrackerListener implements TripObserver {

    private final String observerId;
    private final List<TripEvent> detectedPriceDrops = new ArrayList<>();

    public PriceDropAlertTrackerListener(String observerId) {
        this.observerId = observerId;
    }

    @Override
    public void onTripEvent(TripEvent event) {
        if (event.type() == TripEventType.PRICE_DROP_ALERT) {
            detectedPriceDrops.add(event);
            System.out.printf("  [PriceDropTracker] Market alert logged: %s\n", event.message());
        }
    }

    @Override
    public String getObserverId() {
        return observerId;
    }

    public List<TripEvent> getDetectedPriceDrops() {
        return Collections.unmodifiableList(detectedPriceDrops);
    }
}
