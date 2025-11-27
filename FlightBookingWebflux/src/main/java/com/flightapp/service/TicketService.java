package com.flightapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightapp.entity.Flight;
import com.flightapp.entity.FlightType;
import com.flightapp.entity.Passenger;
import com.flightapp.entity.Ticket;
import com.flightapp.repository.FlightRepository;
import com.flightapp.repository.PassengerRepository;
import com.flightapp.repository.TicketRepository;
import com.flightapp.repository.UserRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TicketService {
	@Autowired
	private FlightRepository flightRepository;
    @Autowired
    private UserRepository userRepository;
	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private PassengerRepository passengerRepository;

	public Mono<String> bookTicket(Long userId, Long departureFlightId, Long returnFlightId,
            List<Passenger> passengers, FlightType tripType) {

int seatCount = passengers.size();

return userRepository.findById(userId)
.switchIfEmpty(Mono.error(() -> new RuntimeException("User not found")))
.flatMap(user ->
flightRepository.findById(departureFlightId)
.switchIfEmpty(Mono.error(() -> new RuntimeException("Departure flight not found")))
.flatMap(departureFlight -> {

 Mono<Flight> returnFlightMono = Mono.empty();
 if (tripType == FlightType.ROUND_TRIP) {
     returnFlightMono = flightRepository.findById(returnFlightId)
             .switchIfEmpty(Mono.error(() -> new RuntimeException("Return flight not found")));
 }

 return returnFlightMono.defaultIfEmpty(null)
     .flatMap(returnFlight -> {

         if (departureFlight.getAvailableSeats() < seatCount) {
             return Mono.error(new RuntimeException("Not enough seats in departure flight"));
         }
         if (returnFlight != null && returnFlight.getAvailableSeats() < seatCount) {
             return Mono.error(new RuntimeException("Not enough seats in return flight"));
         }

         departureFlight.setAvailableSeats(departureFlight.getAvailableSeats() - seatCount);
         Mono<Flight> depSave = flightRepository.save(departureFlight);

         Mono<Flight> retSave = returnFlight != null
                 ? flightRepository.save(returnFlight)
                 : Mono.empty();

         Ticket ticket = new Ticket();
         ticket.setUserId(user.getId());
         ticket.setDepartureFlightId(departureFlight.getId());
         ticket.setReturnFlightId(returnFlight != null ? returnFlight.getId() : null);
         ticket.setTripType(tripType);
         ticket.setBookingTime(LocalDateTime.now());
         ticket.setPnr(UUID.randomUUID().toString().substring(0, 8));
         ticket.setNumberOfSeats(seatCount);

         double total = departureFlight.getPrice() * seatCount;
         if (returnFlight != null) {
             total += returnFlight.getPrice() * seatCount;
         }
         ticket.setTotalPrice(total);

         return Mono.when(depSave, retSave)
             .then(ticketRepository.save(ticket)) 
             .flatMap(savedTicket -> {
                 passengers.forEach(p -> p.setTicketId(savedTicket.getId()));

                 return passengerRepository.saveAll(passengers).collectList()
                         .thenReturn(savedTicket.getPnr());
             });
     });
}));
}

	   public Flux<Ticket> getHistory(String email) {
		   return userRepository.findByEmail(email)
	                .switchIfEmpty(Mono.error( new RuntimeException("User not found")))
	                .flatMapMany(user->ticketRepository.findByUserId(user.getId()));
	    }
	   public Mono<Ticket> getTicketByPnr(String pnr) {
		    return ticketRepository.findByPnr(pnr)
		        .switchIfEmpty(Mono.error(new RuntimeException("No ticket found with this PNR")));
		}
       public Flux<Ticket> getAllTickets(){
    	   return ticketRepository.findAll();
       }
       public Mono<String> cancelTicket(String pnr, String email) {
    	    return ticketRepository.findByPnr(pnr)
    	        .switchIfEmpty(Mono.error(new RuntimeException("No ticket found with this PNR")))
    	        .flatMap(ticket ->
    	            userRepository.findById(ticket.getUserId())
    	                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
    	                .flatMap(user -> {
    	                    if (!user.getEmail().equals(email)) {
    	                        return Mono.just("You cannot cancel another user's ticket");
    	                    }
    	                    if (ticket.isCanceled()) {
    	                        return Mono.just("Ticket is already cancelled");
    	                    }

    	                    return passengerRepository.findByTicketId(ticket.getId()).collectList()
    	                        .flatMap(passengers -> {
    	                            int seats = passengers.size();

    	                            return flightRepository.findById(ticket.getDepartureFlightId())
    	                                .switchIfEmpty(Mono.error(new RuntimeException("Departure flight not found")))
    	                                .flatMap(depFlight -> {
    	                                    LocalDateTime departureTime = depFlight.getDepartureTime();

    	                                    if (LocalDateTime.now().plusHours(24).isAfter(departureTime)) {
    	                                        return Mono.just("Cannot cancel within 24 hours of travel");
    	                                    }

    	                                    depFlight.setAvailableSeats(depFlight.getAvailableSeats() + seats);
    	                                    Mono<Flight> depSave = flightRepository.save(depFlight);

    	                                    Mono<Flight> retSave = Mono.empty();
    	                                    if (ticket.getReturnFlightId() != null) {
    	                                        retSave = flightRepository.findById(ticket.getReturnFlightId())
    	                                                .flatMap(retFlight -> {
    	                                                    retFlight.setAvailableSeats(retFlight.getAvailableSeats() + seats);
    	                                                    return flightRepository.save(retFlight);
    	                                                });
    	                                    }

    	                                    ticket.setCanceled(true);
    	                                    Mono<Ticket> ticketSave = ticketRepository.save(ticket);

    	                                    return Mono.when(depSave, retSave, ticketSave)
    	                                            .then(Mono.just("Cancelled Successfully"));
    	                                });
    	                        });
    	                })
    	        );
    	}


       
       
}
