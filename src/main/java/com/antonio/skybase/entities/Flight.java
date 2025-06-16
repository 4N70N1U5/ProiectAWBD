package com.antonio.skybase.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalTime;

@Entity
@Table(name = "flights")
@Data
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Flight number must not be blank")
    private String number;

    @ManyToOne
    @JoinColumn(name = "departure_airport_id", nullable = false)
    @NotNull(message = "Departure airport must not be null")
    private Airport departureAirport;

    @ManyToOne
    @JoinColumn(name = "arrival_airport_id", nullable = false)
    @NotNull(message = "Arrival airport must not be null")
    private Airport arrivalAirport;

    @Column(name = "departure_time", nullable = false)
    @NotNull(message = "Departure time must not be null")
    private LocalTime departureTime;

    @Column(name = "arrival_time", nullable = false)
    @NotNull(message = "Arrival time must not be null")
    private LocalTime arrivalTime;

    @Column(nullable = false)
    @NotNull(message = "Distance must not be null")
    @Positive(message = "Distance must be positive")
    private Integer distance;
}
