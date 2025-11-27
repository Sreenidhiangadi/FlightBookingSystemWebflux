package com.flightapp.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import com.flightapp.entity.*;
import com.flightapp.service.*;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/v1.0/flight")
public class UserController {

    private final AuthService authService;
    private final FlightService flightService;
    private final TicketService ticketService;

    public UserController(AuthService authService, FlightService flightService, TicketService ticketService) {
        this.authService = authService;
        this.flightService = flightService;
        this.ticketService = ticketService;
    }

    @PostMapping("/user/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<User> register(@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/user/login")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<String>> userLogin(@RequestBody User user) {
        return authService.login(user.getEmail(), user.getPassword())
                .map(session -> ResponseEntity.ok(session))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(401).body(e.getMessage())));
    }

    @PostMapping("/search")
    @ResponseStatus(HttpStatus.CREATED)
    public Flux<Flight> searchFlights(@RequestBody Map<String, String> body) {
        String from = body.get("fromPlace");
        String to = body.get("toPlace");
        LocalDateTime start = LocalDateTime.parse(body.get("start"));
        LocalDateTime end = LocalDateTime.parse(body.get("end"));

        return flightService.searchFlights(from, to, start, end);
    }

    @PostMapping("/search/airline")
    @ResponseStatus(HttpStatus.CREATED)
    public Flux<Flight> searchByAirline(@RequestBody Map<String, String> body) {
        String from = body.get("fromPlace");
        String to = body.get("toPlace");
        String airline = body.get("airline");

        return flightService.searchFlightsByAirline(from, to, airline);
    }

    @PostMapping("/booking")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> bookTicket(@RequestBody Map<String, Object> body) {
        Long userId = Long.parseLong(body.get("userId").toString());
        Long departureFlightId = Long.parseLong(body.get("departureFlightId").toString());
        FlightType tripType = FlightType.valueOf(body.get("tripType").toString());

        Long returnFlightId = body.containsKey("returnFlightId")
                ? Long.parseLong(body.get("returnFlightId").toString())
                : null;

        List<Map<String, Object>> passengerList = (List<Map<String, Object>>) body.get("passengers");
        List<Passenger> passengers = new ArrayList<>();

        passengerList.forEach(p -> {
            Passenger ps = new Passenger();
            ps.setName((String) p.get("name"));
            ps.setAge(((Number) p.get("age")).intValue());
            ps.setGender((String) p.get("gender"));
            ps.setSeatNumber((String) p.get("seatNumber"));
            ps.setMealPreference((String) p.getOrDefault("mealPreference", null));
            passengers.add(ps);
        });

        return ticketService.bookTicket(userId, departureFlightId, returnFlightId, passengers, tripType);
    }

    @GetMapping("/allflights")
    public Flux<Flight> getAllFlights() {
        return flightService.getAllFlights();
    }

    @GetMapping("/ticket/{pnr}")
    public Mono<Ticket> getTicket(@PathVariable String pnr) {
        return ticketService.getTicketByPnr(pnr);
    }

    @GetMapping("/booking/history/{email}")
    public Flux<Ticket> history(@PathVariable String email) {
        return ticketService.getHistory(email);
    }

    @DeleteMapping("/booking/cancel/{pnr}")
    public Mono<String> cancel(@PathVariable String pnr, @RequestParam String email) {
        return ticketService.cancelTicket(pnr, email);
    }

}
