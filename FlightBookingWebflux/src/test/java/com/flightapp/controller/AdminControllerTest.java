package com.flightapp.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.flightapp.entity.Flight;
import com.flightapp.entity.Ticket;
import com.flightapp.entity.User;
import com.flightapp.service.AuthService;
import com.flightapp.service.FlightService;
import com.flightapp.service.TicketService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class AdminControllerTest {

	private AuthService authService;
	private FlightService flightService;
	private TicketService ticketService;
	private AdminController adminController;

	private User user;
	private Flight flight;
	private Ticket ticket;
	@BeforeEach
	void setUp() {
	    authService = mock(AuthService.class);
	    flightService = mock(FlightService.class);
	    ticketService = mock(TicketService.class);
	    adminController = new AdminController(authService, flightService, ticketService);

	    user = new User();
	    user.setId(1L);             
	    user.setEmail("admin@example.com");
	    user.setPassword("password");
	    flight = new Flight();
	    flight.setId(1L);            
	    flight.setAirline("TestFlight");
	    flight.setFromPlace("CityA");
	    flight.setToPlace("CityB");
	    flight.setDepartureTime(LocalDateTime.now().plusDays(1));
	    flight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
	    flight.setTotalSeats(100);
	    flight.setAvailableSeats(100);
	    flight.setPrice(200);

	    ticket = new Ticket();
	    ticket.setId(1L);           
	    ticket.setPnr("PNR123");
	}


	@Test
	void testAdminLogin() {
		when(authService.login(user.getEmail(), user.getPassword())).thenReturn(Mono.just("Login successful"));

		StepVerifier.create(adminController.adminLogin(user)).expectNext("Login successful").verifyComplete();

		verify(authService, times(1)).login(user.getEmail(), user.getPassword());
	}

	@Test
	void testGetAdmin() {
		when(authService.getAdmin(user.getEmail())).thenReturn(Mono.just(user));

		StepVerifier.create(adminController.getAdmin(user)).expectNext(user).verifyComplete();

		verify(authService, times(1)).getAdmin(user.getEmail());
	}

	@Test
	void testGetAllTickets() {
		when(ticketService.getAllTickets()).thenReturn(Flux.just(ticket));

		StepVerifier.create(adminController.getAllTickets()).expectNext(ticket).verifyComplete();

		verify(ticketService, times(1)).getAllTickets();
	}

	@Test
	void testUpdateFlight() {
	    Long flightId = 1L; 

	    Map<String, Object> updates = new HashMap<>();
	    updates.put("airline", "UpdatedAirline");
	    updates.put("price", 500);

	    Flight updatedFlight = new Flight();
	    updatedFlight.setId(flightId);  
	    updatedFlight.setAirline("UpdatedAirline");
	    updatedFlight.setPrice(500);
	    updatedFlight.setFromPlace("CityA");
	    updatedFlight.setToPlace("CityB");
	    updatedFlight.setDepartureTime(LocalDateTime.now().plusDays(1));
	    updatedFlight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
	    updatedFlight.setTotalSeats(100);
	    updatedFlight.setAvailableSeats(100);

	    when(flightService.updateFlight(flightId, updates)).thenReturn(Mono.just(updatedFlight));

	    StepVerifier.create(adminController.update(flightId, updates))
	            .expectNextMatches(f -> f.getAirline().equals("UpdatedAirline") && f.getPrice() == 500)
	            .verifyComplete();

	    verify(flightService, times(1)).updateFlight(flightId, updates);
	}
	@Test
	void testAddFlight() {
		when(flightService.addFlight(flight)).thenReturn(Mono.empty());

		StepVerifier.create(adminController.addFlight(flight)).expectNext("Flight added successfully").verifyComplete();

		verify(flightService, times(1)).addFlight(flight);
	}

	@Test
	void testDeleteFlight() {
	    Long flightId = 1L; 
	    when(flightService.deleteFlight(flightId)).thenReturn(Mono.just("Flight deleted successfully"));

	    StepVerifier.create(adminController.delete(flightId))
	            .expectNext("Flight deleted successfully")
	            .verifyComplete();

	    verify(flightService, times(1)).deleteFlight(flightId);
	}
}