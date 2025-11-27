package com.flightapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightapp.entity.Flight;
import com.flightapp.repository.FlightRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class FlightService {
	@Autowired
public FlightRepository flightRepository;
	public Mono<Flight> addFlight( Flight flight) {
		return flightRepository.save(flight);
	}
	public Mono<String> deleteFlight(Long id) {
	    return flightRepository.findById(id)
	            .switchIfEmpty(Mono.error(new RuntimeException("Flight not found")))
	            .flatMap(flight -> flightRepository.deleteById(id)
	                    .thenReturn("Flight deleted successfully"));
	}

	public Flux<Flight> getAllFlights(){
		return flightRepository.findAll();
	}
	public Mono<Flight> updateFlight(Long id, Map<String, Object> updates) {
	    return flightRepository.findById(id)
	            .switchIfEmpty(Mono.error(new RuntimeException("Flight not found")))
	            .flatMap(flight -> {
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
	                    try {
	                        flight.setDepartureTime(LocalDateTime.parse(updates.get("departureTime").toString()));
	                    } catch (Exception e) {
	                        return Mono.error(new RuntimeException("Invalid departureTime format"));
	                    }
	                }

	                if (updates.containsKey("arrivalTime")) {
	                    flight.setArrivalTime(LocalDateTime.parse((String) updates.get("arrivalTime")));
	                }
	                if (updates.containsKey("price")) {
	                    flight.setPrice(Integer.valueOf(updates.get("price").toString()));
	                }
	                if (updates.containsKey("totalSeats")) {
	                    flight.setTotalSeats(Integer.valueOf(updates.get("totalSeats").toString()));
	                }
	                if (updates.containsKey("availableSeats")) {
	                    flight.setAvailableSeats(Integer.valueOf(updates.get("availableSeats").toString()));
	                }

	                return flightRepository.save(flight);
	            });
	}


	public Mono<Flight> searchFlightById( Long id) {
		return flightRepository.findById(id).switchIfEmpty(Mono.error(new RuntimeException("Flight with this id is not present")));
		
	}
	public Flux<Flight> searchFlights(String fromPlace,String toPlace,LocalDateTime start,LocalDateTime end) {
        return flightRepository.findByFromPlaceAndToPlaceAndDepartureTimeBetween(fromPlace, toPlace, start, end);
    }
	 public Flux<Flight> searchFlightsByAirline(String fromPlace,String toPlace, String airline){
	        return flightRepository.findByFromPlaceAndToPlaceAndAirline(fromPlace, toPlace, airline);
	    }
}
