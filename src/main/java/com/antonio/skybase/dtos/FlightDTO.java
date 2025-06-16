package com.antonio.skybase.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalTime;

@Data
public class FlightDTO {
    private Integer id;

    @NotBlank(message = "Flight number must not be blank")
    private String number;

    @NotNull(message = "Departure airport ID must not be null")
    private Integer departureAirportId;

    @NotNull(message = "Arrival airport ID must not be null")
    private Integer arrivalAirportId;

    @NotNull(message = "Departure time must not be null")
    private LocalTime departureTime;

    @NotNull(message = "Arrival time must not be null")
    private LocalTime arrivalTime;

    @NotNull(message = "Distance must not be null")
    @Positive(message = "Distance must be positive")
    private Integer distance;
}