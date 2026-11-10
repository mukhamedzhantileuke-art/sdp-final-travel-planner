package kz.aitu.sdp.finalproject.observer;

/**
 * Event categories broadcasted to travel itinerary subscribers.
 */
public enum TripEventType {
    ITINERARY_CREATED,
    ROUTE_OPTIMIZED,
    BOOKING_CONFIRMED,
    FLIGHT_DELAYED,
    PRICE_DROP_ALERT,
    ITINERARY_CANCELLED
}
