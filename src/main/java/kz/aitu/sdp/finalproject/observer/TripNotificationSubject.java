package kz.aitu.sdp.finalproject.observer;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Concrete Subject in Observer Pattern (Student 2).
 * Thread-safe implementation managing dynamic subscriber registries per {@link TripEventType}.
 */
public class TripNotificationSubject implements TripSubject {

    private final String subjectName;
    private final ConcurrentMap<TripEventType, CopyOnWriteArrayList<TripObserver>> registry;
    private final AtomicLong dispatchedEventCount;

    public TripNotificationSubject(String subjectName) {
        this.subjectName = Objects.requireNonNull(subjectName, "subjectName cannot be null");
        this.registry = new ConcurrentHashMap<>();
        this.dispatchedEventCount = new AtomicLong(0);
    }

    @Override
    public void subscribe(TripEventType type, TripObserver observer) {
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(observer, "observer cannot be null");
        registry.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).addIfAbsent(observer);
    }

    @Override
    public void unsubscribe(TripEventType type, TripObserver observer) {
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(observer, "observer cannot be null");
        CopyOnWriteArrayList<TripObserver> list = registry.get(type);
        if (list != null) {
            list.remove(observer);
        }
    }

    @Override
    public void notifyObservers(TripEvent event) {
        Objects.requireNonNull(event, "event cannot be null");
        List<TripObserver> observers = registry.get(event.type());
        if (observers != null && !observers.isEmpty()) {
            for (TripObserver observer : observers) {
                observer.onTripEvent(event);
            }
        }
        dispatchedEventCount.incrementAndGet();
    }

    @Override
    public int getObserverCount(TripEventType type) {
        List<TripObserver> observers = registry.get(type);
        return observers != null ? observers.size() : 0;
    }

    public long getDispatchedEventCount() {
        return dispatchedEventCount.get();
    }

    public String getSubjectName() {
        return subjectName;
    }
}
