package com.flightapp.repository;
import java.time.LocalDateTime;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.flightapp.entity.Flight;

import reactor.core.publisher.Flux;
public interface FlightRepository extends ReactiveCrudRepository<Flight,Long>{
Flux<Flight> findByFromPlaceAndToPlaceAndDepartureTimeBetween(String fromPlace, String toPlace, LocalDateTime start, LocalDateTime end);

Flux<Flight> findByFromPlaceAndToPlaceAndAirline(String fromPlace, String toPlace, String airline);
}