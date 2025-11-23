package com.flightapp.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1.0/flight/airline")
public class AdminController {

	private final AuthService authService;
	private final FlightService flightService;
	private final TicketService ticketService;

	public AdminController(AuthService authService, FlightService flightService, TicketService ticketService) {
		this.authService = authService;
		this.flightService = flightService;
		this.ticketService = ticketService;
	}

	@PostMapping("/admin/login")
	@ResponseStatus(HttpStatus.OK)
	public Mono<String> adminLogin(@RequestBody User user) {
		return authService.login(user.getEmail(), user.getPassword());

	}

	@PostMapping("/getadmin")
	@ResponseStatus(HttpStatus.OK)
	public Mono<User> getAdmin(@RequestBody User user) {
		return authService.getAdmin(user.getEmail());

	}

	@GetMapping("/inventory/allTickets")
	public Flux<Ticket> getAllTickets() {
		return ticketService.getAllTickets();
	}

	@PostMapping("/inventory/add")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<String> addFlight(@RequestBody Flight flight) {
		return flightService.addFlight(flight).thenReturn("Flight added successfully");
	}

	@PutMapping("/inventory/update/{id}")
	public Mono<Flight> update(@PathVariable String id, @RequestBody Map<String, Object> updates) {
		return flightService.updateFlight(id, updates);
	}

	@DeleteMapping("/inventory/delete/{id}")
	public Mono<String> delete(@PathVariable String id) {
		return flightService.deleteFlight(id);
	}
}
