package kz.aitu.sdp.finalproject.observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Concrete Observer 1 (Student 2): Dispatches electronic emails with travel documents.
 */
public class EmailNotificationListener implements TripObserver {

    private final String observerId;
    private final String recipientEmail;
    private final List<TripEvent> receivedEvents = new ArrayList<>();

    public EmailNotificationListener(String observerId, String recipientEmail) {
        this.observerId = observerId;
        this.recipientEmail = recipientEmail;
    }

    @Override
    public void onTripEvent(TripEvent event) {
        receivedEvents.add(event);
        System.out.printf("  [EmailService -> %s] New Email: Subject: [%s] | Body: %s\n",
                recipientEmail, event.type(), event.message());
    }

    @Override
    public String getObserverId() {
        return observerId;
    }

    public List<TripEvent> getReceivedEvents() {
        return Collections.unmodifiableList(receivedEvents);
    }
}
