package com.flightapp.repository;


import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.flightapp.entity.Ticket;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TicketRepository extends ReactiveCrudRepository<Ticket,Long>{
	Mono<Ticket> findByPnr(String pnr);
    Flux<Ticket> findByUserId(Long userId);
}
