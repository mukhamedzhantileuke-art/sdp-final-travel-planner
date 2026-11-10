package kz.aitu.sdp.finalproject.observer;

/**
 * Observer Interface in Observer Pattern (Student 2).
 * Implemented by downstream notification channels and analytical listeners.
 */
public interface TripObserver {

    /**
     * Callback invoked when a trip event occurs.
     */
    void onTripEvent(TripEvent event);

    /**
     * Unique identifier for logging and registry management.
     */
    String getObserverId();
}
