package com.flightapp.service;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.flightapp.entity.Passenger;
import com.flightapp.repository.PassengerRepository;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class PassengerServiceTest {

    private PassengerRepository passengerRepository;
    private PassengerService passengerService;

    private Passenger passenger1;
    private Passenger passenger2;

    @BeforeEach
    void setUp() {
        passengerRepository = mock(PassengerRepository.class);
        passengerService = new PassengerService(passengerRepository); 

        passenger1 = new Passenger();
        passenger1.setId(1L);             
        passenger1.setName("John Doe");
        passenger1.setTicketId(100L);      

        passenger2 = new Passenger();
        passenger2.setId(2L);              
        passenger2.setName("Jane Smith");
        passenger2.setTicketId(100L);      
    }

    @Test
    void testGetAllPassengers() {
        when(passengerRepository.findAll()).thenReturn(Flux.just(passenger1, passenger2));

        StepVerifier.create(passengerService.getAllPassengers())
                .expectNext(passenger1)
                .expectNext(passenger2)
                .verifyComplete();

        verify(passengerRepository, times(1)).findAll();
    }

    @Test
    void testGetPassengersByTicketId() {
        Long ticketId = 100L;
        when(passengerRepository.findByTicketId(ticketId)).thenReturn(Flux.just(passenger1, passenger2));

        StepVerifier.create(passengerService.getPassengersByTicketId(ticketId))
                .expectNext(passenger1)
                .expectNext(passenger2)
                .verifyComplete();

        verify(passengerRepository, times(1)).findByTicketId(ticketId);
    }

    @Test
    void testGetPassengersByTicketId_Empty() {
        Long ticketId = 200L;
        when(passengerRepository.findByTicketId(ticketId)).thenReturn(Flux.empty());

        StepVerifier.create(passengerService.getPassengersByTicketId(ticketId))
                .verifyComplete();

        verify(passengerRepository, times(1)).findByTicketId(ticketId);
    }
}
