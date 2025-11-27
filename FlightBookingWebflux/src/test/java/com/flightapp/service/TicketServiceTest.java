package com.flightapp.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.flightapp.entity.Flight;
import com.flightapp.entity.FlightType;
import com.flightapp.entity.Passenger;
import com.flightapp.entity.Ticket;
import com.flightapp.entity.User;
import com.flightapp.repository.FlightRepository;
import com.flightapp.repository.TicketRepository;
import com.flightapp.repository.UserRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class TicketServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketService ticketService;

    private User user;
    private Flight depFlight;
    private Flight retFlight;
    private Passenger passenger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);  // Long id

        depFlight = new Flight();
        depFlight.setId(1L);  // Long id
        depFlight.setAvailableSeats(5);
        depFlight.setPrice(100);

        retFlight = new Flight();
        retFlight.setId(2L);
        retFlight.setAvailableSeats(5);
        retFlight.setPrice(100);

        passenger = new Passenger();
        passenger.setSeatNumber("1A");
    }

    @Test
    void testBookTicket_Success_OneWay() {
        when(userRepository.findById(1L)).thenReturn(Mono.just(user));
        when(flightRepository.findById(1L)).thenReturn(Mono.just(depFlight));
        when(flightRepository.save(depFlight)).thenReturn(Mono.just(depFlight));

        Ticket savedTicket = new Ticket();
        savedTicket.setPnr("PNR1234");
        when(ticketRepository.save(any())).thenReturn(Mono.just(savedTicket));

        StepVerifier.create(ticketService.bookTicket(1L, 1L, null, Collections.singletonList(passenger), FlightType.ONE_WAY))
                .expectNextMatches(pnr -> pnr != null && !pnr.isEmpty())
                .verifyComplete();
    }

    @Test
    void testBookTicket_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(ticketService.bookTicket(1L, 1L, null, Collections.singletonList(passenger), FlightType.ONE_WAY))
                .expectErrorMessage("User not found")
                .verify();
    }

    @Test
    void testBookTicket_DepartureFlightNotFound() {
        when(userRepository.findById(1L)).thenReturn(Mono.just(user));
        when(flightRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(ticketService.bookTicket(1L, 1L, null, Collections.singletonList(passenger), FlightType.ONE_WAY))
                .expectErrorMessage("Departure flight not found")
                .verify();
    }

    @Test
    void testBookTicket_NotEnoughSeats_Departure() {
        depFlight.setAvailableSeats(0);
        when(userRepository.findById(1L)).thenReturn(Mono.just(user));
        when(flightRepository.findById(1L)).thenReturn(Mono.just(depFlight));

        StepVerifier.create(ticketService.bookTicket(1L, 1L, null, Collections.singletonList(passenger), FlightType.ONE_WAY))
                .expectErrorMessage("Not enough seats in departure flight")
                .verify();
    }

    @Test
    void testBookTicket_ReturnFlightNotFound() {
        when(userRepository.findById(1L)).thenReturn(Mono.just(user));
        when(flightRepository.findById(1L)).thenReturn(Mono.just(depFlight));
        when(flightRepository.save(depFlight)).thenReturn(Mono.just(depFlight));
        when(flightRepository.findById(2L)).thenReturn(Mono.empty());

        StepVerifier.create(ticketService.bookTicket(1L, 1L, 2L, Collections.singletonList(passenger), FlightType.ROUND_TRIP))
                .expectErrorMessage("Return flight not found")
                .verify();
    }

    @Test
    void testBookTicket_NotEnoughSeats_Return() {
        retFlight.setAvailableSeats(0);

        when(userRepository.findById(1L)).thenReturn(Mono.just(user));
        when(flightRepository.findById(1L)).thenReturn(Mono.just(depFlight));
        when(flightRepository.save(depFlight)).thenReturn(Mono.just(depFlight));
        when(flightRepository.findById(2L)).thenReturn(Mono.just(retFlight));

        StepVerifier.create(ticketService.bookTicket(1L, 1L, 2L, Collections.singletonList(passenger), FlightType.ROUND_TRIP))
                .expectErrorMessage("Not enough seats in return flight")
                .verify();
    }

    @Test
    void testGetHistory_success() {
        Ticket ticket = new Ticket();
        ticket.setUserId(1L);

        when(userRepository.findByEmail("sreenidhi@test.com")).thenReturn(Mono.just(user));
        when(ticketRepository.findByUserId(1L)).thenReturn(Flux.just(ticket));

        StepVerifier.create(ticketService.getHistory("sreenidhi@test.com"))
                .expectNext(ticket)
                .verifyComplete();
    }

    @Test
    void testGetHistory_UserNotFound() {
        when(userRepository.findByEmail("sreenidhi@test.com")).thenReturn(Mono.empty());

        StepVerifier.create(ticketService.getHistory("sreenidhi@test.com"))
                .expectErrorMessage("User not found")
                .verify();
    }

    @Test
    void testGetTicketByPnr_found() {
        Ticket ticket = new Ticket();
        ticket.setPnr("PNR123");

        when(ticketRepository.findByPnr("PNR123")).thenReturn(Mono.just(ticket));

        StepVerifier.create(ticketService.getTicketByPnr("PNR123"))
                .expectNext(ticket)
                .verifyComplete();
    }

    @Test
    void testGetTicketByPnr_notFound() {
        when(ticketRepository.findByPnr("PNR123")).thenReturn(Mono.empty());

        StepVerifier.create(ticketService.getTicketByPnr("PNR123"))
                .expectErrorMessage("No ticket found")
                .verify();
    }

    @Test
    void testCancelTicket_PNRNotFound() {
        when(ticketRepository.findByPnr("PNR1")).thenReturn(Mono.empty());

        StepVerifier.create(ticketService.cancelTicket("PNR1","U1"))
                .expectErrorMessage("PNR not found")
                .verify();
    }

    @Test
    void testCancelTicket_AlreadyCancelled() {
        Ticket ticket = new Ticket();
        ticket.setCanceled(true);

        when(ticketRepository.findByPnr("PNR1")).thenReturn(Mono.just(ticket));

        StepVerifier.create(ticketService.cancelTicket("PNR1","U1"))
                .expectNext("Ticket already cancelled")
                .verifyComplete();
    }

    @Test
    void testCancelTicket_Success() {
        Ticket ticket = new Ticket();
        ticket.setDepartureFlightId(1L);
        ticket.setCanceled(false);

        when(ticketRepository.findByPnr("PNR1")).thenReturn(Mono.just(ticket));
        when(flightRepository.findById(1L)).thenReturn(Mono.just(depFlight));
        when(flightRepository.save(depFlight)).thenReturn(Mono.just(depFlight));
        when(ticketRepository.save(ticket)).thenReturn(Mono.just(ticket));

        StepVerifier.create(ticketService.cancelTicket("PNR1","U1"))
                .expectNext("Cancelled Successfully")
                .verifyComplete();
    }

    @Test
    void testGetAllTickets() {
        when(ticketRepository.findAll()).thenReturn(Flux.just(new Ticket(), new Ticket()));

        StepVerifier.create(ticketService.getAllTickets())
                .expectNextCount(2)
                .verifyComplete();
    }
}
