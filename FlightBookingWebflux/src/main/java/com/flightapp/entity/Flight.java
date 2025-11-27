package com.flightapp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@Table("flight")
public class Flight {

	  @Id
	  private Long id;
	  @NotBlank(message="Airline name is required")
	  private String airline;
	  @NotBlank(message="From place is required")
	  private String fromPlace;
	  @NotBlank(message="To place is required")
	  private String toPlace;
	  
	  @NotNull(message="Departure time  is required")
	  private LocalDateTime departureTime;
	  @NotNull(message="Arrival time  is required")
	  private LocalDateTime arrivalTime;
	  @Min(value=1,message="Price must be atleast 1")
	  private int price;
	  
	  @Min(value=1,message="Total seats must be atleast 1")
	  private int totalSeats;
	  @Min(value=0,message="Available seats cant be neagtive")
	  private int availableSeats;
	 
	  
}

