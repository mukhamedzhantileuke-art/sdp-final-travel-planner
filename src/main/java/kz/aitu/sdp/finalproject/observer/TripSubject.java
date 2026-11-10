package kz.aitu.sdp.finalproject.observer;

/**
 * Subject Interface in Observer Pattern (Student 2).
 * Allows dynamic runtime subscription and unsubscription per event category.
 */
public interface TripSubject {

    void subscribe(TripEventType type, TripObserver observer);

    void unsubscribe(TripEventType type, TripObserver observer);

    void notifyObservers(TripEvent event);

    int getObserverCount(TripEventType type);
}
