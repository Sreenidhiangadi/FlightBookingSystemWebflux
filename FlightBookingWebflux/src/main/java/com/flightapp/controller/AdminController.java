package com.flightapp.controller;

import com.flightapp.entity.Flight;
import com.flightapp.entity.Ticket;
import com.flightapp.entity.User;
import com.flightapp.service.AuthService;
import com.flightapp.service.FlightService;
import com.flightapp.service.TicketService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/api/v1.0/flight/airline")
public class AdminController {

    private final AuthService authService;
    private final FlightService flightService;
    private final TicketService ticketService;
    public AdminController(AuthService authService, FlightService flightService,TicketService ticketService) {
        this.authService = authService;
        this.flightService = flightService;
        this.ticketService=ticketService;
    }

    @PostMapping("/admin/login")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> adminLogin(@RequestBody User user) {
        return  authService.login(user.getEmail(), user.getPassword());
  
    }
    @PostMapping("/getadmin")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<User> getAdmin(@RequestBody User user) {
    	return authService.getAdmin(user.getEmail());
    	 
    }
    @GetMapping("/inventory/allTickets")
    public Flux<Ticket> getAllTickets(){
    	return ticketService.getAllTickets();
    }
    @PostMapping("/inventory/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> addFlight(@RequestBody Flight flight) {
        return flightService.addFlight(flight)
       .thenReturn("Flight added successfully");
    }
    @PutMapping("/inventory/update/{id}")
    public Mono<Flight> update(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return flightService.updateFlight(id, updates);
    }


    @DeleteMapping("/inventory/delete/{id}")
    public Mono<String> delete(@PathVariable Long id) {
        return flightService.deleteFlight(id);
    }
}
