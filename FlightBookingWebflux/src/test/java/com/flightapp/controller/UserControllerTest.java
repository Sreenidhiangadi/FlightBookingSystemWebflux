package com.flightapp.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.*;

import com.flightapp.entity.*;
import com.flightapp.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class UserControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private FlightService flightService;

    @Mock
    private TicketService ticketService;

    private UserController userController;

    private User user;
    private Flight flight;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(authService, flightService, ticketService);

        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPassword("password");

        flight = new Flight();
        flight.setId(1L);
        flight.setAirline("AirlineA");
        flight.setFromPlace("CityA");
        flight.setToPlace("CityB");
        flight.setDepartureTime(LocalDateTime.now().plusDays(1));
        flight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
        flight.setPrice(500);
        flight.setTotalSeats(100);
        flight.setAvailableSeats(100);

        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setPnr("PNR123");
        ticket.setUserId(1L);
        ticket.setDepartureFlightId(1L);
        ticket.setReturnFlightId(null);
        ticket.setTripType(FlightType.ONE_WAY);
        ticket.setBookingTime(LocalDateTime.now());
    }

    @Test
    void testRegister() {
        when(authService.register(user)).thenReturn(Mono.just(user));

        StepVerifier.create(userController.register(user))
                .expectNext(user)
                .verifyComplete();

        verify(authService, times(1)).register(user);
    }

    @Test
    void testUserLogin_Success() {
        when(authService.login(user.getEmail(), user.getPassword()))
                .thenReturn(Mono.just("Login successful"));

        StepVerifier.create(userController.userLogin(user))
                .expectNextMatches(resp -> resp.getBody().equals("Login successful"))
                .verifyComplete();

        verify(authService, times(1)).login(user.getEmail(), user.getPassword());
    }

    @Test
    void testUserLogin_Failure() {
        when(authService.login(user.getEmail(), user.getPassword()))
                .thenReturn(Mono.error(new RuntimeException("Invalid credentials")));

        StepVerifier.create(userController.userLogin(user))
                .expectNextMatches(resp -> resp.getStatusCodeValue() == 401 && resp.getBody().equals("Invalid credentials"))
                .verifyComplete();

        verify(authService, times(1)).login(user.getEmail(), user.getPassword());
    }

    @Test
    void testSearchFlights() {
        Map<String, String> body = new HashMap<>();
        body.put("fromPlace", "CityA");
        body.put("toPlace", "CityB");
        body.put("start", LocalDateTime.now().toString());
        body.put("end", LocalDateTime.now().plusDays(2).toString());

        when(flightService.searchFlights("CityA", "CityB", 
                LocalDateTime.parse(body.get("start")), LocalDateTime.parse(body.get("end"))))
                .thenReturn(Flux.just(flight));

        StepVerifier.create(userController.searchFlights(body))
                .expectNext(flight)
                .verifyComplete();

        verify(flightService, times(1)).searchFlights("CityA", "CityB",
                LocalDateTime.parse(body.get("start")), LocalDateTime.parse(body.get("end")));
    }

    @Test
    void testSearchByAirline() {
        Map<String, String> body = new HashMap<>();
        body.put("fromPlace", "CityA");
        body.put("toPlace", "CityB");
        body.put("airline", "AirlineA");

        when(flightService.searchFlightsByAirline("CityA", "CityB", "AirlineA"))
                .thenReturn(Flux.just(flight));

        StepVerifier.create(userController.searchByAirline(body))
                .expectNext(flight)
                .verifyComplete();

        verify(flightService, times(1)).searchFlightsByAirline("CityA", "CityB", "AirlineA");
    }

    @Test
    void testBookTicket_OneWay() {
        Map<String, Object> body = new HashMap<>();
        body.put("userId", 1L);
        body.put("departureFlightId", 1L);
        body.put("tripType", "ONE_WAY");
        body.put("passengers", List.of(
                Map.of("name", "John Doe", "age", 25, "gender", "Male", "seatNumber", "1A")
        ));

        when(ticketService.bookTicket(eq(1L), eq(1L), eq(null), anyList(), eq(FlightType.ONE_WAY)))
                .thenReturn(Mono.just("PNR123"));

        StepVerifier.create(userController.bookTicket(body))
                .expectNext("PNR123")
                .verifyComplete();

        verify(ticketService, times(1)).bookTicket(eq(1L), eq(1L), eq(null), anyList(), eq(FlightType.ONE_WAY));
    }

    @Test
    void testGetAllFlights() {
        when(flightService.getAllFlights()).thenReturn(Flux.just(flight));

        StepVerifier.create(userController.getAllFlights())
                .expectNext(flight)
                .verifyComplete();

        verify(flightService, times(1)).getAllFlights();
    }

    @Test
    void testGetTicket() {
        when(ticketService.getTicketByPnr("PNR123")).thenReturn(Mono.just(ticket));

        StepVerifier.create(userController.getTicket("PNR123"))
                .expectNext(ticket)
                .verifyComplete();

        verify(ticketService, times(1)).getTicketByPnr("PNR123");
    }

    @Test
    void testHistory() {
        when(ticketService.getHistory("user@example.com")).thenReturn(Flux.just(ticket));

        StepVerifier.create(userController.history("user@example.com"))
                .expectNext(ticket)
                .verifyComplete();

        verify(ticketService, times(1)).getHistory("user@example.com");
    }

    @Test
    void testCancelTicket() {
        when(ticketService.cancelTicket("PNR123", "user@example.com"))
                .thenReturn(Mono.just("Cancelled Successfully"));

        StepVerifier.create(userController.cancel("PNR123", "user@example.com"))
                .expectNext("Cancelled Successfully")
                .verifyComplete();

        verify(ticketService, times(1)).cancelTicket("PNR123", "user@example.com");
    }

}
