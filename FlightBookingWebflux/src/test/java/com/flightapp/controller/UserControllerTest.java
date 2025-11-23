package com.flightapp.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.flightapp.entity.Flight;
import com.flightapp.entity.FlightType;
import com.flightapp.entity.Ticket;
import com.flightapp.entity.User;
import com.flightapp.service.AuthService;
import com.flightapp.service.FlightService;
import com.flightapp.service.TicketService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class UserControllerTest {

	private AuthService authService;
	private FlightService flightService;
	private TicketService ticketService;
	private UserController userController;

	private User user;
	private Flight flight;
	private Ticket ticket;

	@BeforeEach
	void setUp() {
		authService = mock(AuthService.class);
		flightService = mock(FlightService.class);
		ticketService = mock(TicketService.class);
		userController = new UserController(authService, flightService, ticketService);

		user = new User();
		user.setEmail("user@example.com");
		user.setPassword("password");

		flight = new Flight();
		flight.setId("F1");
		flight.setAirline("AirlineA");
		flight.setFromPlace("CityA");
		flight.setToPlace("CityB");
		flight.setDepartureTime(LocalDateTime.now().plusDays(1));
		flight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
		flight.setPrice(500);
		flight.setTotalSeats(100);
		flight.setAvailableSeats(100);

		ticket = new Ticket();
		ticket.setPnr("PNR123");
		ticket.setUserId("U1");
		ticket.setDepartureFlightId("F1");
		ticket.setReturnFlightId(null);
		ticket.setTripType(FlightType.ONE_WAY);
		ticket.setBookingTime(LocalDateTime.now());
		ticket.setPassengers(List.of());
	}

	@Test
	void testRegister() {
		when(authService.register(user)).thenReturn(Mono.just(user));

		StepVerifier.create(userController.register(user)).expectNext(user).verifyComplete();

		verify(authService, times(1)).register(user);
	}

	@Test
	void testUserLogin_Success() {
		when(authService.login(user.getEmail(), user.getPassword())).thenReturn(Mono.just("Login successful"));

		StepVerifier.create(userController.userLogin(user)).expectNext("Login successful").verifyComplete();

		verify(authService, times(1)).login(user.getEmail(), user.getPassword());
	}

	@Test
	void testUserLogin_Failure() {
		when(authService.login(user.getEmail(), user.getPassword())).thenReturn(Mono.empty());

		StepVerifier.create(userController.userLogin(user)).expectNext("Invalid credentials").verifyComplete();
	}

	@Test
	void testSearchFlights() {
		when(flightService.searchFlights(flight.getFromPlace(), flight.getToPlace(), flight.getDepartureTime(),
				flight.getArrivalTime())).thenReturn(Flux.just(flight));

		StepVerifier.create(userController.searchFlights(flight)).expectNext(flight).verifyComplete();

		verify(flightService, times(1)).searchFlights(flight.getFromPlace(), flight.getToPlace(),
				flight.getDepartureTime(), flight.getArrivalTime());
	}

	@Test
	void testSearchByAirline() {
		Map<String, String> body = Map.of("fromPlace", "CityA", "toPlace", "CityB", "airline", "AirlineA");

		when(flightService.searchFlightsByAirline("CityA", "CityB", "AirlineA")).thenReturn(Flux.just(flight));

		StepVerifier.create(userController.searchByAirline(body)).expectNext(flight).verifyComplete();

		verify(flightService, times(1)).searchFlightsByAirline("CityA", "CityB", "AirlineA");
	}

	@Test
	void testGetAllFlights() {
		when(flightService.getAllFlights()).thenReturn(Flux.just(flight));

		StepVerifier.create(userController.getAllFlights()).expectNext(flight).verifyComplete();

		verify(flightService, times(1)).getAllFlights();
	}

	@Test
	void testBookTicket_Success() {
		when(ticketService.bookTicket("U1", "F1", null, List.of(), FlightType.ONE_WAY)).thenReturn(Mono.just("PNR123"));

		StepVerifier.create(userController.bookTicket(Mono.just(ticket)))
				.expectNextMatches(response -> response.getBody().get("pnr").equals("PNR123")).verifyComplete();

		verify(ticketService, times(1)).bookTicket("U1", "F1", null, List.of(), FlightType.ONE_WAY);
	}

	@Test
	void testBookTicket_Error() {
		when(ticketService.bookTicket("U1", "F1", null, List.of(), FlightType.ONE_WAY))
				.thenReturn(Mono.error(new RuntimeException("Booking failed")));

		StepVerifier.create(userController.bookTicket(Mono.just(ticket)))
				.expectNextMatches(response -> response.getStatusCode().is4xxClientError()
						&& response.getBody().get("error").equals("Booking failed"))
				.verifyComplete();
	}

	@Test
	void testGetTicket() {
		when(ticketService.getTicketByPnr("PNR123")).thenReturn(Mono.just(ticket));

		StepVerifier.create(userController.getTicket("PNR123")).expectNext(ticket).verifyComplete();

		verify(ticketService, times(1)).getTicketByPnr("PNR123");
	}

	@Test
	void testHistory() {
		when(ticketService.getHistory("user@example.com")).thenReturn(Flux.just(ticket));

		StepVerifier.create(userController.history("user@example.com")).expectNext(ticket).verifyComplete();

		verify(ticketService, times(1)).getHistory("user@example.com");
	}

	@Test
	void testCancel() {
		when(ticketService.cancelTicket("PNR123")).thenReturn(Mono.just("Ticket cancelled"));

		StepVerifier.create(userController.cancel("PNR123")).expectNext("Ticket cancelled").verifyComplete();

		verify(ticketService, times(1)).cancelTicket("PNR123");
	}
}