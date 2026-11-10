package kz.aitu.sdp.finalproject.observer;

import java.util.Objects;

/**
 * Immutable domain event dispatched by the trip notification system.
 */
public record TripEvent(
        String eventId,
        TripEventType type,
        String itineraryId,
        String message,
        long timestamp
) {
    public TripEvent {
        Objects.requireNonNull(eventId, "eventId cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(itineraryId, "itineraryId cannot be null");
        Objects.requireNonNull(message, "message cannot be null");
    }

    public static TripEvent of(String eventId, TripEventType type, String itineraryId, String message) {
        return new TripEvent(eventId, type, itineraryId, message, System.currentTimeMillis());
    }
}
