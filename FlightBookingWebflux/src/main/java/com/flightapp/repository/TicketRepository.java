package com.flightapp.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.flightapp.entity.Ticket;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TicketRepository extends ReactiveMongoRepository<Ticket, String> {
	Mono<Ticket> findByPnr(String pnr);

	Flux<Ticket> findByUserId(String userId);
}
