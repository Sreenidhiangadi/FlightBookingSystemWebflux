package com.flightapp.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.flightapp.entity.Flight;
import com.flightapp.repository.FlightRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FlightService {
	public FlightRepository flightRepository;
	 public FlightService(FlightRepository flightRepository) {
	        this.flightRepository = flightRepository;
	    }
	public Mono<Flight> addFlight(Flight flight) {
		return flightRepository.save(flight);
	}

	public Mono<String> deleteFlight(String id) {
		return flightRepository.findById(id).switchIfEmpty(Mono.error(new RuntimeException("Flight not found")))
				.flatMap(flight -> flightRepository.deleteById(id).thenReturn("Flight deleted successfully"));
	}

	public Flux<Flight> getAllFlights() {
		return flightRepository.findAll();
	}

	public Mono<Flight> updateFlight(String id, Map<String, Object> updates) {
	    return flightRepository.findById(id)
	            .switchIfEmpty(Mono.error(new RuntimeException("Flight not found")))
	            .flatMap(flight -> {
	                try {
	                    if (updates.containsKey("airline")) {
	                        flight.setAirline((String) updates.get("airline"));
	                    }
	                    if (updates.containsKey("fromPlace")) {
	                        flight.setFromPlace((String) updates.get("fromPlace"));
	                    }
	                    if (updates.containsKey("toPlace")) {
	                        flight.setToPlace((String) updates.get("toPlace"));
	                    }
	                    if (updates.containsKey("departureTime")) {
	                        flight.setDepartureTime(LocalDateTime.parse(updates.get("departureTime").toString()));
	                    }
	                    if (updates.containsKey("arrivalTime")) {
	                        flight.setArrivalTime(LocalDateTime.parse(updates.get("arrivalTime").toString()));
	                    }
	                    if (updates.containsKey("price")) {
	                        flight.setPrice(Integer.parseInt(updates.get("price").toString()));
	                    }
	                    if (updates.containsKey("totalSeats")) {
	                        flight.setTotalSeats(Integer.parseInt(updates.get("totalSeats").toString()));
	                    }
	                    if (updates.containsKey("availableSeats")) {
	                        flight.setAvailableSeats(Integer.parseInt(updates.get("availableSeats").toString()));
	                    }
	                } catch (Exception e) {
	                    return Mono.error(new RuntimeException("Invalid input format: " + e.getMessage()));
	                }

	                return flightRepository.save(flight);
	            });
	}


	public Mono<Flight> searchFlightById(String id) {
		return flightRepository.findById(id)
				.switchIfEmpty(Mono.error(new RuntimeException("Flight with this id is not present")));

	}

	public Flux<Flight> searchFlights(String fromPlace, String toPlace, LocalDateTime start, LocalDateTime end) {
		return flightRepository.findByFromPlaceAndToPlaceAndDepartureTimeBetween(fromPlace, toPlace, start, end);
	}

	public Flux<Flight> searchFlightsByAirline(String fromPlace, String toPlace, String airline) {
		return flightRepository.findByFromPlaceAndToPlaceAndAirline(fromPlace, toPlace, airline);
	}
}
