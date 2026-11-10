package kz.aitu.sdp.finalproject.observer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pattern 6 Tests: Observer Pattern (Student 2)")
class TripNotificationObserverTest {

    private TripNotificationSubject subject;

    @BeforeEach
    void setUp() {
        subject = new TripNotificationSubject("TestNotificationEngine");
    }

    @Test
    @DisplayName("Test 19: Observers receive events filtered by subscription type")
    void testEventDeliveryByType() {
        EmailNotificationListener emailListener = new EmailNotificationListener("EMAIL-1", "test@travel.com");
        SmsNotificationListener smsListener = new SmsNotificationListener("SMS-1", "+7-777-111-2233");

        subject.subscribe(TripEventType.BOOKING_CONFIRMED, emailListener);
        subject.subscribe(TripEventType.FLIGHT_DELAYED, smsListener);

        // Emit booking confirmed
        subject.notifyObservers(TripEvent.of("E1", TripEventType.BOOKING_CONFIRMED, "ITIN-1", "Booking paid"));

        assertEquals(1, emailListener.getReceivedEvents().size());
        assertEquals(0, smsListener.getReceivedEvents().size(), "SMS listener should not receive booking confirmed event");

        // Emit flight delayed
        subject.notifyObservers(TripEvent.of("E2", TripEventType.FLIGHT_DELAYED, "ITIN-1", "Flight delayed by 2h"));

        assertEquals(1, emailListener.getReceivedEvents().size());
        assertEquals(1, smsListener.getReceivedEvents().size());
    }

    @Test
    @DisplayName("Test 20: Observers can dynamically unsubscribe at runtime")
    void testDynamicUnsubscribe() {
        EmailNotificationListener emailListener = new EmailNotificationListener("EMAIL-1", "test@travel.com");
        subject.subscribe(TripEventType.PRICE_DROP_ALERT, emailListener);

        subject.notifyObservers(TripEvent.of("E1", TripEventType.PRICE_DROP_ALERT, "ITIN-1", "Price down $20"));
        assertEquals(1, emailListener.getReceivedEvents().size());

        subject.unsubscribe(TripEventType.PRICE_DROP_ALERT, emailListener);

        subject.notifyObservers(TripEvent.of("E2", TripEventType.PRICE_DROP_ALERT, "ITIN-1", "Price down $50"));
        assertEquals(1, emailListener.getReceivedEvents().size(), "Unsubscribed observer should not receive further alerts");
    }

    @Test
    @DisplayName("Test 21: Observers can safely unsubscribe from within their own event callback")
    void testSelfUnsubscriptionWithinHandler() {
        AtomicInteger count = new AtomicInteger(0);
        AtomicBoolean threwException = new AtomicBoolean(false);

        TripObserver selfUnsubscribingObserver = new TripObserver() {
            @Override
            public void onTripEvent(TripEvent event) {
                count.incrementAndGet();
                try {
                    subject.unsubscribe(TripEventType.BOOKING_CONFIRMED, this);
                } catch (Exception e) {
                    threwException.set(true);
                }
            }

            @Override
            public String getObserverId() {
                return "SELF-UNSUB";
            }
        };

        subject.subscribe(TripEventType.BOOKING_CONFIRMED, selfUnsubscribingObserver);

        assertDoesNotThrow(() -> subject.notifyObservers(TripEvent.of("E1", TripEventType.BOOKING_CONFIRMED, "IT-1", "First")));
        assertFalse(threwException.get());
        assertEquals(1, count.get());

        // Second notification must not reach observer
        subject.notifyObservers(TripEvent.of("E2", TripEventType.BOOKING_CONFIRMED, "IT-1", "Second"));
        assertEquals(1, count.get());
    }
}
