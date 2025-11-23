package com.flightapp.service;

import org.springframework.stereotype.Service;

import com.flightapp.entity.Passenger;
import com.flightapp.repository.PassengerRepository;

import reactor.core.publisher.Flux;

@Service
public class PassengerService {

	private final PassengerRepository passengerRepository;

	public PassengerService(PassengerRepository passengerRepository) {
		this.passengerRepository = passengerRepository;
	}

	public Flux<Passenger> getAllPassengers() {
		return passengerRepository.findAll();
	}

	public Flux<Passenger> getPassengersByTicketId(String ticketId) {
		return passengerRepository.findByTicketId(ticketId);
	}
}
