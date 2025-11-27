package com.flightapp.entity;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Table("ticket")
public class Ticket {

    @Id
    private Long id;

    @NotBlank(message = "PNR cannot be blank")
    @Column("pnr")
    private String pnr;

    @NotNull(message = "User ID is required")
    @Column("user_id")
    private Long userId;

    @NotNull(message = "Departure flight is required")
    @Column("departure_flight_id")
    private Long departureFlightId;

    @Column("return_flight_id")
    private Long returnFlightId;

    @Column("trip_type")
    private FlightType tripType;

    @Column("booking_time")
    private LocalDateTime bookingTime;

    @Column("number_of_seats")
    private int numberOfSeats;

    @Column("meal_type")
    private String mealType;

    @Column("total_price")
    private Double totalPrice;

    @Column("canceled")
    private boolean canceled = false;
}
