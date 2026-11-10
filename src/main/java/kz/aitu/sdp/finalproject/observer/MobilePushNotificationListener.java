package kz.aitu.sdp.finalproject.observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Concrete Observer 3 (Student 2): Dispatches in-app mobile push notifications to traveler device.
 */
public class MobilePushNotificationListener implements TripObserver {

    private final String observerId;
    private final String deviceToken;
    private final List<TripEvent> receivedEvents = new ArrayList<>();

    public MobilePushNotificationListener(String observerId, String deviceToken) {
        this.observerId = observerId;
        this.deviceToken = deviceToken;
    }

    @Override
    public void onTripEvent(TripEvent event) {
        receivedEvents.add(event);
        System.out.printf("  [MobilePush -> %s] Push Banner: %s\n", deviceToken, event.message());
    }

    @Override
    public String getObserverId() {
        return observerId;
    }

    public List<TripEvent> getReceivedEvents() {
        return Collections.unmodifiableList(receivedEvents);
    }
}
