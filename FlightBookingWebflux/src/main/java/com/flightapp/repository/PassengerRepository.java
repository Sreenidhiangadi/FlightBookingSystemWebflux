package com.flightapp.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.flightapp.entity.Passenger;

import reactor.core.publisher.Flux;

public interface PassengerRepository extends ReactiveCrudRepository<Passenger,Long> {
	Flux<Passenger> findByTicketId(Long ticketId);

}
