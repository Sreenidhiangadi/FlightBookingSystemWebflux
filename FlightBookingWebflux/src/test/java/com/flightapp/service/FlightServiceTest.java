package com.flightapp.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.flightapp.entity.Flight;
import com.flightapp.repository.FlightRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

	@Mock
	private FlightRepository flightRepository;

	@InjectMocks
	private FlightService flightService;

	private Flight flight;

	@BeforeEach
	void setup() {
		flight = new Flight();
		flight.setId(1L);
		flight.setAirline("Indigo");
		flight.setFromPlace("BLR");
		flight.setToPlace("DEL");
		flight.setDepartureTime(LocalDateTime.now());
		flight.setArrivalTime(LocalDateTime.now().plusHours(2));
		flight.setPrice(5000);
		flight.setTotalSeats(120);
		flight.setAvailableSeats(100);
	}

	@Test
	void testAddFlight() {

	    when(flightRepository
	            .findByFromPlaceAndToPlaceAndAirline(flight.getFromPlace(),
	                    flight.getToPlace(),
	                    flight.getAirline()))
	            .thenReturn(Flux.empty()); 

	    when(flightRepository.save(flight)).thenReturn(Mono.just(flight));

	    StepVerifier.create(flightService.addFlight(flight))
	            .expectNext(flight)    // <-- expect Flight object, not String
	            .verifyComplete();

	    verify(flightRepository, times(1)).findByFromPlaceAndToPlaceAndAirline(
	            flight.getFromPlace(), flight.getToPlace(), flight.getAirline());
	    verify(flightRepository, times(1)).save(flight);
	}




	@Test
	void testDeleteFlight_Success() {
		when(flightRepository.findById(1L)).thenReturn(Mono.just(flight));
		when(flightRepository.deleteById(1L)).thenReturn(Mono.empty());

		StepVerifier.create(flightService.deleteFlight(1L)).expectNext("Flight deleted successfully").verifyComplete();
	}

	@Test
	void testDeleteFlight_NotFound() {
		when(flightRepository.findById(2L)).thenReturn(Mono.empty());

		StepVerifier.create(flightService.deleteFlight(2L))
				.expectErrorMatches(e -> e.getMessage().contains("Flight not found")).verify();
	}

	@Test
	void testGetAllFlights() {
		when(flightRepository.findAll()).thenReturn(Flux.just(flight));

		StepVerifier.create(flightService.getAllFlights()).expectNext(flight).verifyComplete();
	}

	@Test
	void testUpdateFlightSuccess() {
		Map<String, Object> updates = new HashMap<>();
		updates.put("airline", "Air India");
		updates.put("price", 6000);
		updates.put("totalSeats", 150);
		updates.put("availableSeats", 120);

		when(flightRepository.findById(1L)).thenReturn(Mono.just(flight));
		when(flightRepository.save(any(Flight.class))).thenReturn(Mono.just(flight));

		StepVerifier.create(flightService.updateFlight(1L, updates))
				.expectNextMatches(updated -> updated.getAirline().equals("Air India") && updated.getPrice() == 6000
						&& updated.getTotalSeats() == 150 && updated.getAvailableSeats() == 120)
				.verifyComplete();
	}

	@Test
	void testUpdateFlight_NotFound() {
		when(flightRepository.findById(2L)).thenReturn(Mono.empty());

		StepVerifier.create(flightService.updateFlight(2L, new HashMap<>()))
				.expectErrorMatches(e -> e.getMessage().contains("Flight not found")).verify();
	}

	@Test
	void testSearchFlightById_NotFound() {
		when(flightRepository.findById(2L)).thenReturn(Mono.empty());

		StepVerifier.create(flightService.searchFlightById(2L))
				.expectErrorMatches(e -> e.getMessage().contains("Flight with this id is not present")).verify();
	}

	@Test
	void testSearchFlightById_Success() {
		when(flightRepository.findById(2L)).thenReturn(Mono.just(flight));

		StepVerifier.create(flightService.searchFlightById(2L)).expectNext(flight).verifyComplete();
	}

	@Test
	void testSearchFlights() {
		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plusHours(4);

		when(flightRepository.findByFromPlaceAndToPlaceAndDepartureTimeBetween("BLR", "DEL", start, end))
				.thenReturn(Flux.just(flight));

		StepVerifier.create(flightService.searchFlights("BLR", "DEL", start, end)).expectNext(flight).verifyComplete();
	}

	@Test
	void testSearchFlightsByAirline() {
		when(flightRepository.findByFromPlaceAndToPlaceAndAirline("BLR", "DEL", "Indigo"))
				.thenReturn(Flux.just(flight));

		StepVerifier.create(flightService.searchFlightsByAirline("BLR", "DEL", "Indigo")).expectNext(flight)
				.verifyComplete();
	}
}
