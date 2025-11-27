package com.flightapp.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Document(collection = "tickets")
public class Ticket {

	@Id
	private String id;

	
	private String pnr;

	@NotBlank(message = "User ID is required")
	private String userId;

	@NotBlank(message = "Departure flight is required")
	private String departureFlightId;

	private String returnFlightId;

	@NotNull(message = "Trip type is required")
	private FlightType tripType;

	
	private LocalDateTime bookingTime;

	private String seatsBooked;

	private String mealType;

	@Min(value = 0, message = "Total price must be positive")
	private Double totalPrice;

	private boolean canceled;
	@Transient
	private List<Passenger> passengers;
}
