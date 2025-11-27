package com.flightapp.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.flightapp.entity.Flight;
import com.flightapp.entity.Ticket;
import com.flightapp.entity.User;
import com.flightapp.service.AuthService;
import com.flightapp.service.FlightService;
import com.flightapp.service.TicketService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/flight")
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
	public Mono<User> register(@Valid @RequestBody User user) {
		return authService.register(user);
	}

	@PostMapping("/user/login")
	public Mono<String> userLogin(@Valid @RequestBody User user) {
		return authService.login(user.getEmail(), user.getPassword()).switchIfEmpty(Mono.just("Invalid credentials"));
	}

	@PostMapping("/search")
	public Flux<Flight> searchFlights(@Valid @RequestBody Flight f) {
		return flightService.searchFlights(f.getFromPlace(), f.getToPlace(), f.getDepartureTime(), f.getArrivalTime());
	}

	@PostMapping("/search/airline")
	public Flux<Flight> searchByAirline(@Valid @RequestBody Map<String, String> body) {
		return flightService.searchFlightsByAirline(body.get("fromPlace"), body.get("toPlace"), body.get("airline"));
	}

	@GetMapping("/allflights")
	public Flux<Flight> getAllFlights() {
		return flightService.getAllFlights();
	}

	@PostMapping("/booking")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<ResponseEntity<Map<String, String>>> bookTicket(@Valid @RequestBody Ticket ticket) {
	    return ticketService.bookTicket(
	            ticket.getUserId(),
	            ticket.getDepartureFlightId(),
	            ticket.getReturnFlightId(),
	            ticket.getPassengers(),
	            ticket.getTripType()
	        )
	        .map(pnr -> ResponseEntity.ok(Map.of("pnr", pnr)));
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
	public Mono<String> cancel(@PathVariable String pnr) {
		return ticketService.cancelTicket(pnr);
	}

}