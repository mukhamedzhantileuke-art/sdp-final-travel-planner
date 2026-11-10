# Final Project: Travel Itinerary Planner
**Course:** Software Design Patterns (AITU, SE-2531)  
**Team (2 Students):**
* **Student 1 (Mukhamedzhan Tileuke):** Builder, Factory Method, Facade
* **Student 2 (Teammate):** Adapter, Strategy, Observer
**Technology Stack:** Java 17, JUnit 5, JaCoCo, Maven, PlantUML

---

## 1. Project Overview & Business Domain

The **Travel Itinerary Planner** is a cohesive, enterprise-grade travel management platform designed to help globetrotters plan, customize, optimize, book, and track complex multi-destination vacations.

Instead of navigating disparate booking engines, foreign flight APIs, and disjointed hotel systems, a traveler can assemble an end-to-end itinerary in a single unified workflow.

### The 6 Design Patterns in Action:
```
                                TRAVEL ITINERARY PLANNER
                                           │
         ┌─────────────────────────────────┴─────────────────────────────────┐
         ▼                                                                   ▼
   STUDENT 1 (Creational & Facade)                     STUDENT 2 (Integration & Behavioral)
  ─────────────────────────────────                   ─────────────────────────────────────
  1. Builder (Creational)                             4. Strategy (Behavioral)
     TripItineraryBuilder for complex trips              Route Optimization (Fastest, Cheapest, Scenic)
  2. Factory Method (Creational)                      5. Adapter (Structural)
     BookingFactory (Flight, Hotel, Activity)            AmadeusFlightAdapter for legacy XML GDS
  3. Facade (Structural)                              6. Observer (Behavioral)
     TravelBookingFacade (One-click checkout)            Multi-channel notifications (Email, SMS, Push)
```

---

## 2. End-to-End Cohesive Workflow (User Story)

All 6 patterns are tightly integrated into a single business scenario:

1. **Adapter (Student 2)**: The customer searches for flights from Almaty (`ALA`) to Paris (`CDG`). The system queries a legacy 3rd-party GDS (`LegacyAmadeusFlightApi`) via `AmadeusFlightAdapter`, which translates raw legacy XML payloads into clean domain `FlightOption` objects.
2. **Factory Method (Student 1)**: The system instantiates candidate booking components using dedicated factory creators (`FlightBookingFactory`, `HotelBookingFactory`, `ActivityBookingFactory`).
3. **Strategy (Student 2)**: The user sets their travel preference (e.g. *Cheapest* or *Scenic* route). The `RouteOptimizerContext` executes the selected `RouteOptimizationStrategy` to balance budget and experiences.
4. **Builder (Student 1)**: `TripItineraryBuilder` step-by-step constructs the immutable `TripItinerary`, validating origin/destination, chronological dates, traveler passport, and budget limits.
5. **Observer (Student 2)**: `TripNotificationSubject` registers customer notification channels (`EmailNotificationListener`, `SmsNotificationListener`, `MobilePushNotificationListener`, `PriceDropAlertTrackerListener`).
6. **Facade (Student 1)**: The user clicks «Book Entire Trip». `TravelBookingFacade` unifies the flight seat reservation, hotel room hold, payment credit card charge, and insurance policy issuance in one coordinated call.
7. **Observer Alert (Student 2)**: Once checkout succeeds, real-time confirmation events are broadcasted, and subsequent flight delay or price-drop events trigger immediate SMS/Push alerts.

---

## 3. Detailed Pattern Breakdown

### Part A: Student 1 Patterns

#### 1. Builder Pattern (`kz.aitu.sdp.finalproject.builder`)
* **Classes:** `TripItineraryBuilder`, `TripItinerary`
* **Problem Solved:** Constructing a travel itinerary involves 10+ parameters (traveler info, origin, destination, dates, budget, lists of flights, hotels, excursions, special requests). Creating this through a traditional constructor results in an unmaintainable «telescoping constructor» anti-pattern.
* **Solution:** `TripItineraryBuilder` provides a fluent API (`.setOrigin().setDestination().addBooking().build()`), validates business invariants (e.g., end date cannot precede start date, budget cannot be negative), and produces an unmodifiable `TripItinerary` object.

#### 2. Factory Method Pattern (`kz.aitu.sdp.finalproject.factory`)
* **Classes:** 
  * Product: `BookingItem`
  * Concrete Products: `FlightBookingItem`, `HotelBookingItem`, `ActivityBookingItem`
  * Creator: `BookingFactory`
  * Concrete Creators: `FlightBookingFactory`, `HotelBookingFactory`, `ActivityBookingFactory`
* **Problem Solved:** Travel items have divergent booking behaviors, pricing models (per night, per seat, per ticket), and confirmation lifecycles.
* **Solution:** Declares an abstract `createBooking()` method. Adding future reservation types (e.g., `CarRentalBooking`, `TrainBooking`) requires adding a new subclass without touching existing factory code, respecting the **Open/Closed Principle (OCP)**.

#### 3. Facade Pattern (`kz.aitu.sdp.finalproject.facade`)
* **Classes:**
  * Facade: `TravelBookingFacade`
  * Subsystems: `FlightSubsystem`, `HotelSubsystem`, `PaymentSubsystem`, `InsuranceSubsystem`
* **Problem Solved:** Booking an entire vacation requires calling four independent subsystems in the correct sequence, handling failures, and issuing refunds if one step fails. Exposing these to the client violates the Principle of Least Knowledge.
* **Solution:** `TravelBookingFacade` provides high-level methods `bookEntireTrip(...)` and `cancelEntireTrip(...)`. The client only interacts with the facade, keeping client code simple and decoupled from subsystem internals.

---

### Part B: Student 2 Patterns

#### 4. Adapter Pattern (`kz.aitu.sdp.finalproject.adapter`)
* **Classes:**
  * Target: `FlightSearchProvider`
  * Adaptee: `LegacyAmadeusFlightApi`, `AmadeusRawFlightData`
  * Adapter: `AmadeusFlightAdapter`
* **Problem Solved:** The 3rd-party Amadeus global distribution system uses legacy method names (`lookupSchedulesRawXml`), integer fare cents, and raw IATA carrier codes that are incompatible with our domain model.
* **Solution:** `AmadeusFlightAdapter` implements our clean `FlightSearchProvider` interface, calls the legacy API, converts cents to USD dollars, decodes carrier codes (e.g. `KC` $\rightarrow$ Air Astana), and formats timestamps into Java `LocalDateTime`.

#### 5. Strategy Pattern (`kz.aitu.sdp.finalproject.strategy`)
* **Classes:**
  * Strategy Interface: `RouteOptimizationStrategy`
  * Concrete Strategies: `FastestRouteStrategy`, `CheapestRouteStrategy`, `ScenicRouteStrategy`
  * Context: `RouteOptimizerContext`
* **Problem Solved:** Travelers have conflicting optimization priorities (minimizing travel time vs. staying under strict budget vs. maximizing sightseeing). Writing this with nested `if-else` or `switch` violates the Single Responsibility Principle and Open/Closed Principle.
* **Solution:** Each routing algorithm is encapsulated in a dedicated strategy class. The client selects or swaps strategies dynamically at runtime via `RouteOptimizerContext.setStrategy(...)` without conditional branching inside the context.

#### 6. Observer Pattern (`kz.aitu.sdp.finalproject.observer`)
* **Classes:**
  * Subject Interface: `TripSubject`
  * Concrete Subject: `TripNotificationSubject`
  * Observer Interface: `TripObserver`
  * Concrete Observers: `EmailNotificationListener`, `SmsNotificationListener`, `MobilePushNotificationListener`, `PriceDropAlertTrackerListener`
* **Problem Solved:** Customers need real-time flight delay alerts, booking vouchers, and price drop notifications without tight coupling between the booking engine and notification transport protocols.
* **Solution:** `TripNotificationSubject` maintains a thread-safe registry (`ConcurrentHashMap` + `CopyOnWriteArrayList`) per `TripEventType`. Observers subscribe and unsubscribe dynamically at runtime, including safely from within their own event callbacks.

---

## 4. Class Diagram & Architecture

The architecture diagram is provided in [`diagram.puml`](file:///c:/Users/WW/Documents/antigravity/Courses/02_Software_Design_Patterns/Final%20Project.%20Travel%20Itinerary%20Planner/diagram.puml).

```
+---------------------------------------------------------------------------------------------+
|                                    TRAVEL ITINERARY PLANNER                                 |
+---------------------------------------------------------------------------------------------+
| [Student 1]                                           | [Student 2]                         |
|                                                       |                                     |
|  TripItineraryBuilder                                 |  AmadeusFlightAdapter               |
|         │                                             |         │                           |
|         ▼ constructs                                  |         ▼ implements                |
|    TripItinerary ──o BookingItem                      |  FlightSearchProvider (Target)      |
|                        ▲                              |                                     |
|                        │ creates                      |  RouteOptimizerContext              |
|                  BookingFactory                       |         │ delegates to              |
|                                                       |         ▼                           |
|  TravelBookingFacade                                  |  RouteOptimizationStrategy          |
|         │ coordinates                                 |                                     |
|         ▼                                             |  TripNotificationSubject            |
|  Flight / Hotel / Payment / Insurance Subsystems      |         │ broadcasts to             |
|                                                       |         ▼                           |
|                                                       |  TripObserver (Email / SMS / Push)  |
+---------------------------------------------------------------------------------------------+
```

---

## 5. Defense Cheat Sheet (Questions & Answers)

### Questions for Student 1 (Builder, Factory Method, Facade)

**Q1: Why did you use Builder instead of standard constructors or setters?**
> *Answer:* An itinerary has numerous optional and mandatory fields (traveler, dates, multiple bookings, activities, budget). A constructor with 10+ arguments causes the «telescoping constructor» anti-pattern and makes client code prone to argument order errors. Setters would leave the object mutable and allow inconsistent half-initialized states. The Builder validates all fields at once in `.build()` and returns a completely immutable, thread-safe `TripItinerary`.

**Q2: How does your Factory Method adhere to the Open/Closed Principle?**
> *Answer:* The creator `BookingFactory` relies exclusively on the `BookingItem` interface. If we need to add car rentals tomorrow, we create a new `CarRentalBookingItem` and `CarRentalBookingFactory`. We do not need to modify a single line of existing code in `TripItinerary`, `TravelBookingFacade`, or existing factories.

**Q3: How does your Facade satisfy the Principle of Least Knowledge (Law of Demeter)?**
> *Answer:* The client (`Main`) only talks to `TravelBookingFacade.bookEntireTrip()`. The client has zero knowledge of `FlightSubsystem`, `HotelSubsystem`, `PaymentSubsystem`, or `InsuranceSubsystem`. If payment processing or insurance underwriting APIs change, only the Facade is modified; the client remains untouched.

---

### Questions for Student 2 (Adapter, Strategy, Observer)

**Q4: What is the structural difference between your Adapter and Facade?**
> *Answer:* Both wrap classes, but their intent is fundamentally different:
> * **Adapter** converts an existing, incompatible interface (`LegacyAmadeusFlightApi`) into a target interface expected by the client (`FlightSearchProvider`) to make two incompatible systems work together.
> * **Facade** defines a brand new, higher-level interface to simplify access to an entire subsystem of multiple classes (`FlightSubsystem`, `HotelSubsystem`, etc.).

**Q5: Why choose Strategy over placing `if/else` inside the itinerary?**
> *Answer:* Placing routing algorithms directly inside `TripItinerary` with `if (mode == FASTEST)` violates the Single Responsibility Principle and Open/Closed Principle. Any new routing algorithm (e.g., `EcoFriendlyRouteStrategy`) would require altering and re-testing the core itinerary class. Strategy encapsulates each algorithm into its own class and allows runtime swapping.

**Q6: What happens if an Observer unsubscribes while events are being broadcasted?**
> *Answer:* Our `TripNotificationSubject` uses `CopyOnWriteArrayList` stored in a `ConcurrentHashMap`. During `notifyObservers()`, iteration happens over an unmodifiable snapshot of the list. When an observer unsubscribes (even from within its own `onTripEvent` method), a new underlying copy of the array is created for future events without invalidating the active iteration, preventing `ConcurrentModificationException`.

---

## 6. Verification & Test Execution

Run the complete test suite (22 unit & integration tests) using Maven:
```powershell
mvn clean test
```

Run the interactive console application:
```powershell
mvn compile exec:java -Dexec.mainClass="kz.aitu.sdp.finalproject.Main"
```
Or directly with Java:
```powershell
java -cp "target/classes" kz.aitu.sdp.finalproject.Main
```
