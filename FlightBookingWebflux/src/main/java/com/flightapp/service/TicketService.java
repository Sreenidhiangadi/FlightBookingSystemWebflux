package com.flightapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.flightapp.entity.Flight;
import com.flightapp.entity.FlightType;
import com.flightapp.entity.Passenger;
import com.flightapp.entity.Ticket;
import com.flightapp.repository.FlightRepository;
import com.flightapp.repository.TicketRepository;
import com.flightapp.repository.UserRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TicketService {
	private FlightRepository flightRepository;
	private UserRepository userRepository;
	private TicketRepository ticketRepository;

	public TicketService(FlightRepository flightRepository, UserRepository userRepository,
			TicketRepository ticketRepository) {
		this.flightRepository = flightRepository;
		this.userRepository = userRepository;
		this.ticketRepository = ticketRepository;
	}

	public Mono<String> bookTicket(String userId, String departureFlightId, String returnFlightId,
			List<Passenger> passengers, FlightType tripType) {

		int seatCount = passengers.size();

		return userRepository.findById(userId).switchIfEmpty(Mono.error(new RuntimeException("User not found")))
				.flatMap(user -> flightRepository.findById(departureFlightId)
						.switchIfEmpty(Mono.error(new RuntimeException("Departure flight not found")))
						.flatMap(depFlight -> {
							if (depFlight.getAvailableSeats() < seatCount)
								return Mono.error(new RuntimeException("Not enough seats in departure flight"));

							depFlight.setAvailableSeats(depFlight.getAvailableSeats() - seatCount);

							return flightRepository.save(depFlight).flatMap(savedDep -> {
								if (tripType == FlightType.ONE_WAY) {
									Ticket ticket = createTicket(userId, savedDep, null, passengers, tripType);
									return ticketRepository.save(ticket).map(Ticket::getPnr);
								} else {
									return flightRepository.findById(returnFlightId)
											.switchIfEmpty(Mono.error(new RuntimeException("Return flight not found")))
											.flatMap(retFlight -> {
												if (retFlight.getAvailableSeats() < seatCount)
													return Mono.error(
															new RuntimeException("Not enough seats in return flight"));

												retFlight.setAvailableSeats(retFlight.getAvailableSeats() - seatCount);

												return flightRepository.save(retFlight).flatMap(savedRet -> {
													Ticket ticket = createTicket(userId, savedDep, savedRet, passengers,
															tripType);
													return ticketRepository.save(ticket).map(Ticket::getPnr);
												});
											});
								}
							});
						}));
	}

	private Ticket createTicket(String userId, Flight depFlight, Flight retFlight, List<Passenger> passengers,
			FlightType tripType) {
		int seatCount = passengers.size();
		Ticket ticket = new Ticket();
		ticket.setUserId(userId);
		ticket.setDepartureFlightId(depFlight.getId());
		ticket.setReturnFlightId(retFlight != null ? retFlight.getId() : null);
		ticket.setTripType(tripType);
		ticket.setPnr(UUID.randomUUID().toString().substring(0, 8));
		ticket.setBookingTime(LocalDateTime.now());
		String seats = passengers.stream().map(Passenger::getSeatNumber).collect(Collectors.joining(","));
		ticket.setSeatsBooked(seats);

		double total = depFlight.getPrice() * seatCount;
		if (retFlight != null)
			total += retFlight.getPrice() * seatCount;
		ticket.setTotalPrice(total);

		return ticket;
	}

	public Flux<Ticket> getHistory(String email) {
		return userRepository.findByEmail(email).switchIfEmpty(Mono.error(new RuntimeException("User not found")))
				.flatMapMany(user -> ticketRepository.findByUserId(user.getId()));
	}

	public Mono<Ticket> getTicketByPnr(String pnr) {

		return ticketRepository.findByPnr(pnr).switchIfEmpty(Mono.error(new RuntimeException("No ticket found")));

	}

	public Mono<String> cancelTicket(String pnr) {
		return ticketRepository.findByPnr(pnr).switchIfEmpty(Mono.error(new RuntimeException("PNR not found")))
				.flatMap(ticket -> {

					if (ticket.isCanceled()) {
						return Mono.just("Ticket already cancelled");
					}
					final int seatCount;
					if (ticket.getSeatsBooked() != null && !ticket.getSeatsBooked().isEmpty()) {
						seatCount = ticket.getSeatsBooked().split(",").length;
					} else {
						seatCount = 1;
					}

					Mono<Flight> saveDep = flightRepository.findById(ticket.getDepartureFlightId()).flatMap(dep -> {
						dep.setAvailableSeats(dep.getAvailableSeats() + seatCount);
						return flightRepository.save(dep);
					});
					Mono<Flight> saveRet = Mono.empty();
					if (ticket.getReturnFlightId() != null) {
						saveRet = flightRepository.findById(ticket.getReturnFlightId()).flatMap(ret -> {
							ret.setAvailableSeats(ret.getAvailableSeats() + seatCount);
							return flightRepository.save(ret);
						});
					}
					ticket.setCanceled(true);
					Mono<Ticket> saveTicket = ticketRepository.save(ticket);

					return Mono.when(saveDep, saveRet, saveTicket).then(Mono.just("Cancelled Successfully"));
				});
	}

	public Flux<Ticket> getAllTickets() {
		return ticketRepository.findAll();
	}

}