# ASTANA IT UNIVERSITY
### Faculty of Computer Science and Engineering
### Department of Software Engineering

**COURSE:** Software Design Patterns (SE-2531)  
**FINAL PROJECT REPORT:** Travel Itinerary Planner  
**GITHUB REPOSITORY:** https://github.com/mukhamedzhantileuke-art/sdp-final-travel-planner  

**TEAM MEMBERS:**  
* **Student 1:** Mukhamedzhan Tileuke (Group: SE-2531)  
  *Implemented Patterns:* Builder, Factory Method, Facade  
* **Student 2:** Miras Kadrinov (Group: SE-2531)  
  *Implemented Patterns:* Adapter, Strategy, Observer  

**INSTRUCTOR:** Aruzhan Mektapbayeva 
**SEMESTER:** Fall 2026  

---

## 1. Introduction

### 1.1 Problem Statement
Modern travel planning is highly fragmented. A traveler preparing for an international vacation typically has to navigate separate websites or apps: searching for airline tickets on global distribution systems, reserving rooms on hotel platforms, purchasing guided tour tickets, acquiring travel medical insurance, and monitoring real-time flight schedule changes. 

Building an integrated travel planner presents several software engineering challenges:
1. **Heterogeneous External Interfaces:** External airline reservation systems (such as legacy Amadeus GDS) expose proprietary, non-standard APIs with raw XML payloads, integer cents, and concatenated route strings that do not conform to modern clean domain models.
2. **Complex Object Creation:** A multi-day vacation itinerary contains more than ten parameters (traveler info, origin and destination airports, dates, budget constraints, flight tickets, hotel reservations, and excursion passes). Using traditional constructors creates an error-prone "telescoping constructor" anti-pattern.
3. **Dynamic Routing Priorities:** Different travelers have conflicting optimization goals (e.g., minimizing transit duration vs. strictly minimizing cost vs. maximizing tourist sightseeing).
4. **Subsystem Coordination:** Processing the final checkout requires executing steps across four independent domains (payment processing, airline seat holds, hotel room reservation, and insurance policy issuance) with rollback capability upon failure.
5. **Decoupled Notifications:** Customers must be notified immediately when an itinerary is generated, booked, or delayed without coupling the core booking engine to transport protocols (Email, SMS, Mobile Push).

### 1.2 Project Objectives
The objective of this project is to develop an enterprise-grade, object-oriented **Travel Itinerary Planner** application in Java 17 that solves these problems by coordinating six classical Gang of Four (GoF) design patterns into a unified, cohesive system. 

Key goals:
* Enforce SOLID design principles (Single Responsibility, Open/Closed, Dependency Inversion).
* Achieve high cohesion and loose coupling across subsystems.
* Ensure thread safety and immutability for domain models.
* Deliver complete automated test coverage using JUnit 5 (22 unit and integration tests).

### 1.3 Technology Stack
* **Programming Language:** Java 17 (LTS)
* **Build Automation:** Apache Maven 3.9+
* **Testing Framework:** JUnit 5 (Jupiter 5.10.2)
* **Code Coverage:** JaCoCo 0.8.11
* **Architecture Modeling:** PlantUML

---

## 2. System Architecture & Work Distribution

The project was developed collaboratively by two students, with an equal distribution of responsibilities covering Creational, Structural, and Behavioral patterns:

| Team Member | Pattern | Category | Assigned Classes / Responsibilities |
| :--- | :--- | :--- | :--- |
| **Student 1 (Mukhamedzhan Tileuke)** | 1. Builder | Creational | `TripItineraryBuilder`, `TripItinerary` |
| **Student 1 (Mukhamedzhan Tileuke)** | 2. Factory Method | Creational | `BookingFactory`, `FlightBookingFactory`, `HotelBookingFactory`, `ActivityBookingFactory`, `BookingItem` |
| **Student 1 (Mukhamedzhan Tileuke)** | 3. Facade | Structural | `TravelBookingFacade`, `FlightSubsystem`, `HotelSubsystem`, `PaymentSubsystem`, `InsuranceSubsystem` |
| **Student 2 (Miras Kadrinov)** | 4. Adapter | Structural | `AmadeusFlightAdapter`, `FlightSearchProvider`, `LegacyAmadeusFlightApi`, `AmadeusRawFlightData` |
| **Student 2 (Miras Kadrinov)** | 5. Strategy | Behavioral | `RouteOptimizationStrategy`, `FastestRouteStrategy`, `CheapestRouteStrategy`, `ScenicRouteStrategy`, `RouteOptimizerContext` |
| **Student 2 (Miras Kadrinov)** | 6. Observer | Behavioral | `TripSubject`, `TripNotificationSubject`, `TripObserver`, `EmailNotificationListener`, `SmsNotificationListener`, `MobilePushNotificationListener`, `PriceDropAlertTrackerListener` |

---

## 3. Detailed Pattern Implementation

### 3.1 Builder Pattern (Student 1: Mukhamedzhan Tileuke)

#### Intent and Justification
The `TripItinerary` class models the entire vacation schedule. It contains traveler identity, departure and destination airports, chronological date intervals, budget limits, lists of polymorphic booking items, and special requests. 

A traditional constructor with over ten parameters would cause the telescoping constructor anti-pattern, leading to parameter ordering bugs. Providing public setters would leave the object mutable and allow partially initialized or inconsistent states.

The `TripItineraryBuilder` provides a fluent interface (`.setOrigin().setDestination().setDateRange().build()`), validates business constraints (e.g., start date must precede end date, budget cannot be negative), and creates a completely immutable `TripItinerary` instance.

#### Implementation Code Excerpt
```java
public class TripItineraryBuilder {
    private String id;
    private String tripName;
    private Traveler traveler;
    private Location origin;
    private Location destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private double budgetLimit;
    private final List<BookingItem> bookings = new ArrayList<>();
    private final List<String> plannedNotes = new ArrayList<>();
    private TripStatus status = TripStatus.DRAFT;

    public TripItineraryBuilder setDateRange(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        return this;
    }

    public TripItineraryBuilder addBooking(BookingItem booking) {
        if (booking != null) {
            this.bookings.add(booking);
        }
        return this;
    }

    public TripItinerary build() {
        if (tripName == null || tripName.isBlank()) {
            throw new IllegalStateException("Trip name must be provided");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalStateException("End date cannot be earlier than start date");
        }
        return new TripItinerary(id, tripName, traveler, origin, destination,
                                 startDate, endDate, budgetLimit, bookings, plannedNotes, status);
    }
}
```

---

### 3.2 Factory Method Pattern (Student 1: Mukhamedzhan Tileuke)

#### Intent and Justification
An itinerary comprises multiple distinct reservation commodities: flights, hotel accommodations, and guided tours. Each has different booking attributes (seat class vs. room nights vs. excursion duration) and distinct pricing logic.

The Factory Method pattern defines an abstract creator `BookingFactory` with an abstract method `createBooking()`. Specialized creators (`FlightBookingFactory`, `HotelBookingFactory`, `ActivityBookingFactory`) instantiate concrete products conforming to the common `BookingItem` interface.

This design adheres to the **Open/Closed Principle (OCP)**: adding a new reservation category (e.g., `CarRentalBookingItem` with `CarRentalBookingFactory`) requires zero modifications to existing booking factories or client code.

#### Implementation Code Excerpt
```java
public interface BookingItem {
    String getId();
    String getTitle();
    BookingType getType();
    double getCost();
    String getDetails();
    boolean isConfirmed();
    void confirm();
    void cancel();
}

public abstract class BookingFactory {
    public abstract BookingItem createBooking(String id, String primaryTitle, 
                                              String secondaryOption, double costOrRate, double extraNumeric);
}

public class HotelBookingFactory extends BookingFactory {
    @Override
    public BookingItem createBooking(String id, String hotelName, String roomType, 
                                     double pricePerNight, double nights) {
        int numberOfNights = (int) Math.max(1, nights);
        return new HotelBookingItem(id, hotelName, roomType, numberOfNights, pricePerNight);
    }
}
```

---

### 3.3 Facade Pattern (Student 1: Mukhamedzhan Tileuke)

#### Intent and Justification
Executing a trip checkout involves interacting with four independent subsystems:
1. `FlightSubsystem`: reserves airline seats and records passport references.
2. `HotelSubsystem`: verifies room availability and issues room holds.
3. `PaymentSubsystem`: communicates with merchant banking gateways to process card charges.
4. `InsuranceSubsystem`: underwrites comprehensive emergency travel policies.

Without a facade, the client code would have to coordinate all four subsystems, enforce sequential dependencies, handle transaction failures, and perform manual compensation rollbacks.

The `TravelBookingFacade` provides high-level methods (`bookEntireTrip` and `cancelEntireTrip`). The client interacts exclusively with the facade, adhering to the **Principle of Least Knowledge (Law of Demeter)** and decoupling clients from subsystem complexities.

#### Implementation Code Excerpt
```java
public class TravelBookingFacade {
    private final FlightSubsystem flightSubsystem;
    private final HotelSubsystem hotelSubsystem;
    private final PaymentSubsystem paymentSubsystem;
    private final InsuranceSubsystem insuranceSubsystem;

    public BookingResult bookEntireTrip(TripItinerary itinerary, PaymentDetails payment) {
        double totalAmount = itinerary.getTotalCost();

        // 1. Financial transaction
        boolean paymentSuccess = paymentSubsystem.processPayment(payment, totalAmount);
        if (!paymentSuccess) {
            return BookingResult.failure(itinerary.getId(), "Payment authorization declined by merchant bank.");
        }

        // 2. Coordinate reservations across items
        for (BookingItem item : itinerary.getBookings()) {
            if (item instanceof FlightBookingItem flight) {
                flightSubsystem.reserveFlight(flight.getFlightNumber(), itinerary.getTraveler().name(), itinerary.getTraveler().passportNumber());
                flight.confirm();
            } else if (item instanceof HotelBookingItem hotel) {
                hotelSubsystem.reserveRoom(hotel.getHotelName(), itinerary.getTraveler().name(), hotel.getNumberOfNights());
                hotel.confirm();
            } else if (item instanceof ActivityBookingItem activity) {
                activity.confirm();
            }
        }

        // 3. Issue insurance policy
        String policyId = insuranceSubsystem.issuePolicy(itinerary.getTraveler(), itinerary.getDurationDays());
        String confirmationCode = "CONF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return BookingResult.success(itinerary.getId(), confirmationCode, totalAmount, "Trip confirmed. Policy: " + policyId);
    }
}
```

---

### 3.4 Adapter Pattern (Student 2: Miras Kadrinov)

#### Intent and Justification
Our domain application expects flight queries through the clean `FlightSearchProvider` target interface, returning standard `FlightOption` objects. 

However, external global distribution systems (such as `LegacyAmadeusFlightApi`) use legacy method signatures (`lookupSchedulesRawXml`), integer fare cents instead of double dollar amounts, and raw concatenated string tokens (e.g., `"ALA-CDG"`).

The `AmadeusFlightAdapter` implements `FlightSearchProvider` by wrapping the legacy adaptee. It converts date parameters to ISO strings, translates integer cents to USD, decodes IATA carrier codes (e.g., `KC` $\rightarrow$ "Air Astana", `LH` $\rightarrow$ "Lufthansa"), and maps raw timestamps to Java `LocalDateTime`.

#### Implementation Code Excerpt
```java
public interface FlightSearchProvider {
    List<FlightOption> searchAvailableFlights(String originAirport, String destinationAirport, LocalDate departureDate);
}

public class AmadeusFlightAdapter implements FlightSearchProvider {
    private final LegacyAmadeusFlightApi legacyApi;

    public AmadeusFlightAdapter(LegacyAmadeusFlightApi legacyApi) {
        this.legacyApi = legacyApi;
    }

    @Override
    public List<FlightOption> searchAvailableFlights(String origin, String destination, LocalDate departureDate) {
        String formattedDate = departureDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        List<AmadeusRawFlightData> rawData = legacyApi.lookupSchedulesRawXml(origin, destination, formattedDate);

        List<FlightOption> cleanOptions = new ArrayList<>();
        for (AmadeusRawFlightData raw : rawData) {
            String airlineName = CARRIER_NAMES.getOrDefault(raw.getCarrierCode(), raw.getCarrierCode() + " Airlines");
            double priceUsd = raw.getFareInCents() / 100.0;

            cleanOptions.add(new FlightOption(
                    raw.getCarrierCode() + "-" + raw.getFlightNumeric(),
                    airlineName, origin, destination,
                    LocalDateTime.parse(raw.getDepartureIsoTimestamp()),
                    LocalDateTime.parse(raw.getArrivalIsoTimestamp()),
                    priceUsd, raw.getFlightTimeMinutes()
            ));
        }
        return cleanOptions;
    }
}
```

---

### 3.5 Strategy Pattern (Student 2: Miras Kadrinov)

#### Intent and Justification
Travelers prioritize different dimensions when planning a route:
* `FastestRouteStrategy`: prioritizes direct flights, minimal layovers, and central hotels to minimize transit time.
* `CheapestRouteStrategy`: sorts candidate items by price and caps selection strictly under the traveler's budget limit.
* `ScenicRouteStrategy`: prioritizes cultural excursions, tours, and daylight scenic transit.

Embedding these algorithms inside the itinerary class via `if-else` or `switch` statements would violate the **Single Responsibility Principle** and require modifying existing classes whenever a new routing heuristic is added.

The Strategy pattern defines the `RouteOptimizationStrategy` interface. The `RouteOptimizerContext` maintains a reference to the active strategy and supports dynamic algorithm switching at runtime via `setStrategy(...)` without conditional branching inside the context.

#### Implementation Code Excerpt
```java
public interface RouteOptimizationStrategy {
    OptimizedPlan optimizeRoute(Location origin, Location destination, List<BookingItem> candidateBookings, double budgetLimit);
    String getName();
}

public class RouteOptimizerContext {
    private RouteOptimizationStrategy strategy;

    public RouteOptimizerContext(RouteOptimizationStrategy initialStrategy) {
        this.strategy = initialStrategy;
    }

    public void setStrategy(RouteOptimizationStrategy newStrategy) {
        this.strategy = newStrategy;
    }

    public OptimizedPlan executeOptimization(Location origin, Location destination, List<BookingItem> candidateBookings, double budgetLimit) {
        return strategy.optimizeRoute(origin, destination, candidateBookings, budgetLimit);
    }
}
```

---

### 3.6 Observer Pattern (Student 2: Miras Kadrinov)

#### Intent and Justification
A travel planning system must notify customers in real time when key operational events occur: itinerary generation, checkout confirmation, flight delay alerts, and price drops. Coupling the booking facade or routing engine directly to email or SMS sender libraries would violate the **Single Responsibility Principle**.

The Observer pattern provides loose coupling between event publishers and listeners. `TripNotificationSubject` maintains a thread-safe registry of `TripObserver` subscribers using `ConcurrentHashMap` and `CopyOnWriteArrayList` categorized by `TripEventType`. Subscribers can register, unregister, or self-unsubscribe dynamically at runtime without throwing `ConcurrentModificationException`.

#### Implementation Code Excerpt
```java
public interface TripObserver {
    void onTripEvent(TripEvent event);
    String getObserverId();
}

public class TripNotificationSubject implements TripSubject {
    private final ConcurrentMap<TripEventType, CopyOnWriteArrayList<TripObserver>> registry = new ConcurrentHashMap<>();

    @Override
    public void subscribe(TripEventType type, TripObserver observer) {
        registry.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).addIfAbsent(observer);
    }

    @Override
    public void unsubscribe(TripEventType type, TripObserver observer) {
        CopyOnWriteArrayList<TripObserver> list = registry.get(type);
        if (list != null) {
            list.remove(observer);
        }
    }

    @Override
    public void notifyObservers(TripEvent event) {
        List<TripObserver> observers = registry.get(event.type());
        if (observers != null && !observers.isEmpty()) {
            for (TripObserver observer : observers) {
                observer.onTripEvent(event);
            }
        }
    }
}
```

---

## 4. End-to-End Cohesive Workflow

The six patterns collaborate in a realistic, synchronized business process:

1. **External Search (Adapter):** The traveler searches for flights between Almaty (`ALA`) and Paris (`CDG`). `AmadeusFlightAdapter` translates raw GDS data into clean `FlightOption` records.
2. **Item Instantiation (Factory Method):** `FlightBookingFactory`, `HotelBookingFactory`, and `ActivityBookingFactory` create candidate `BookingItem` objects.
3. **Route Optimization (Strategy):** The user selects their optimization preference (e.g., `ScenicRouteStrategy`). `RouteOptimizerContext` optimizes bookings to balance budget and cultural activities.
4. **Itinerary Assembly (Builder):** `TripItineraryBuilder` step-by-step builds the final, immutable `TripItinerary`, validating dates and budget.
5. **Notification Subscription (Observer):** The system subscribes `EmailNotificationListener`, `SmsNotificationListener`, `MobilePushNotificationListener`, and `PriceDropAlertTrackerListener` to the `TripNotificationSubject`.
6. **Checkout Execution (Facade):** The user confirms booking. `TravelBookingFacade` charges the payment card, reserves flight seats, books hotel rooms, and issues insurance in one coordinated call.
7. **Operational Event Dispatch (Observer):** The subject broadcasts `BOOKING_CONFIRMED`, `FLIGHT_DELAYED`, and `PRICE_DROP_ALERT` events, immediately triggering SMS alerts, push banners, and email vouchers.

---

## 5. Testing and Verification

The test suite consists of **22 automated JUnit 5 tests** covering each pattern individually, plus a comprehensive end-to-end integration test.

### Test Breakdown:
* **Builder Tests (`TripItineraryBuilderTest`):** 4 tests validating successful build, missing fields, inverted date checks, and negative budget rejection.
* **Factory Method Tests (`BookingFactoryTest`):** 4 tests verifying flight creation, hotel night calculation, excursion tickets, and polymorphic lifecycles.
* **Facade Tests (`TravelBookingFacadeTest`):** 3 tests verifying one-click checkout, invalid payment decline, and full trip cancellation/refund.
* **Adapter Tests (`AmadeusFlightAdapterTest`):** 3 tests verifying legacy XML adaptation, cents-to-dollars conversion, and IATA carrier mapping.
* **Strategy Tests (`RouteOptimizationStrategyTest`):** 4 tests validating fastest route duration, cheapest route budget capping, scenic tour priorities, and runtime strategy swapping.
* **Observer Tests (`TripNotificationObserverTest`):** 3 tests validating event type filtering, dynamic unsubscription, and safe self-unsubscription inside callbacks.
* **Team Integration Test (`IntegrationEndToEndTest`):** 1 test exercising all six patterns together in a complete booking scenario.

**Maven Test Execution Result:**
```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running kz.aitu.sdp.finalproject.builder.TripItineraryBuilderTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running kz.aitu.sdp.finalproject.facade.TravelBookingFacadeTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running kz.aitu.sdp.finalproject.factory.BookingFactoryTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running kz.aitu.sdp.finalproject.adapter.AmadeusFlightAdapterTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running kz.aitu.sdp.finalproject.strategy.RouteOptimizationStrategyTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running kz.aitu.sdp.finalproject.observer.TripNotificationObserverTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running kz.aitu.sdp.finalproject.IntegrationEndToEndTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] Tests run: 22, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 6. Conclusion

In this project, we successfully engineered a cohesive, extensible Travel Itinerary Planner system in Java 17. By applying six classical design patterns, we achieved:
1. **Maintainability:** Clear separation between business domain logic, external integrations, and notification channels.
2. **Extensibility:** Adding new travel commodities, route strategies, or external flight vendors requires creating new classes rather than modifying existing code.
3. **Robustness:** Complete immutability of core domain entities and full validation covered by 22 automated tests.

---

## 7. Further Work

Future enhancements include:
1. **Persistence Layer:** Integrating Spring Data JPA and PostgreSQL to store itineraries, bookings, and traveler accounts.
2. **Live GDS APIs:** Replacing simulated legacy GDS endpoints with live OAuth-authenticated Amadeus or Sabre REST APIs.
3. **Web User Interface:** Developing a web frontend using React or Vue.js for interactive visual trip planning.
4. **Message Broker:** Offloading notification broadcasts to RabbitMQ or Apache Kafka for asynchronous processing under high user loads.
