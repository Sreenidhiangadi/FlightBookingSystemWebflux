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
	private final FlightRepository flightRepository;

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
	                        applyUpdates(flight, updates);  // extracted method
	                    } catch (Exception e) {
	                        return Mono.error(new RuntimeException("Invalid input format: " + e.getMessage()));
	                    }
	                    return flightRepository.save(flight);
	                });
	    }

	    private void applyUpdates(Flight flight, Map<String, Object> updates) {
	        setIfPresent(updates, "airline", val -> flight.setAirline((String) val));
	        setIfPresent(updates, "fromPlace", val -> flight.setFromPlace((String) val));
	        setIfPresent(updates, "toPlace", val -> flight.setToPlace((String) val));
	        setIfPresent(updates, "departureTime", val -> flight.setDepartureTime(LocalDateTime.parse(val.toString())));
	        setIfPresent(updates, "arrivalTime", val -> flight.setArrivalTime(LocalDateTime.parse(val.toString())));
	        setIfPresent(updates, "price", val -> flight.setPrice(Integer.parseInt(val.toString())));
	        setIfPresent(updates, "totalSeats", val -> flight.setTotalSeats(Integer.parseInt(val.toString())));
	        setIfPresent(updates, "availableSeats", val -> flight.setAvailableSeats(Integer.parseInt(val.toString())));
	    }

	    private <T> void setIfPresent(Map<String, Object> updates, String key, java.util.function.Consumer<Object> setter) {
	        if (updates.containsKey(key)) {
	            setter.accept(updates.get(key));
	        }
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
