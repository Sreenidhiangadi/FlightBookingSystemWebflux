package com.flightapp.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Table("passenger")
public class Passenger {

    @Id
    private Long id;

    @NotBlank(message="User name is required")
    private String name;

    @NotBlank(message="Gender is required")
    private String gender;

    @NotNull(message="Age is required")
    @Min(value = 1, message = "Age must be positive")
    private Integer age;

    @NotBlank(message="Seat number is required")
    @Column("seat_number")
    private String seatNumber;

    private String mealPreference;

    @Column("ticket_id")
    private Long ticketId; 
}
