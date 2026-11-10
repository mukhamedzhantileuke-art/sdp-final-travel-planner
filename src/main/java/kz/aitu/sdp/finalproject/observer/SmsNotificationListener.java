package kz.aitu.sdp.finalproject.observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Concrete Observer 2 (Student 2): Dispatches urgent SMS text messages for real-time alerts.
 */
public class SmsNotificationListener implements TripObserver {

    private final String observerId;
    private final String phoneNumber;
    private final List<TripEvent> receivedEvents = new ArrayList<>();

    public SmsNotificationListener(String observerId, String phoneNumber) {
        this.observerId = observerId;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void onTripEvent(TripEvent event) {
        receivedEvents.add(event);
        System.out.printf("  [SMS Gateway -> %s] SMS: ALERT (%s): %s\n",
                phoneNumber, event.type(), event.message());
    }

    @Override
    public String getObserverId() {
        return observerId;
    }

    public List<TripEvent> getReceivedEvents() {
        return Collections.unmodifiableList(receivedEvents);
    }
}
