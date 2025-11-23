# Flight Booking System WebFlux

The Flight Booking System is a reactive Spring Boot application built using **Spring WebFlux** and **MongoDB**.  
It enables users to browse flights, book tickets, and manage their bookings efficiently using **non-blocking reactive API calls**.

---

##  Key Features
- User Registration & Login (Admin / User roles)
- Add & Manage Flights (Admin)
- Search Available Flights by Route
- Book & View Tickets (User)
- Retrieve All Bookings (Admin/User)
- Reactive Data Streaming with `Flux` & `Mono`
- MongoDB for persistence (No SQL)
- Fully async and scalable using Spring WebFlux

---

## Tech Stack
| Component | Technology |
|----------|------------|
| Backend | Spring Boot WebFlux |
| Database | MongoDB (Reactive) |
| Build Tool | Maven |
| Language | Java |

---

##  What I Learned and Implemented
- Difference between Spring MVC vs WebFlux
- Why reactive programming improves scalability
- Implementing REST APIs with `Mono` & `Flux`
- Handling asynchronous data flow with MongoDB
- CRUD operations for Flights, Users, Tickets

---


```mermaid
erDiagram
    USER ||--o{ TICKET : books
    FLIGHT ||--o{ TICKET : "departure/return"
    TICKET ||--o{ PASSENGER : contains

    USER {
        string id
        string name
        string gender
        int age
        string email
        Role role
    }

    FLIGHT {
        string id
        string airline
        string fromPlace
        string toPlace
        datetime departureTime
        datetime arrivalTime
        int price
        int totalSeats
        int availableSeats
    }

    TICKET {
        string id
        string pnr
        string userId
        string departureFlightId
        string returnFlightId
        FlightType tripType
        datetime bookingTime
        double totalPrice
        boolean canceled
    }

    PASSENGER {
        string id
        string name
        string gender
        int age
        string seatNumber
        string ticketId
    }

