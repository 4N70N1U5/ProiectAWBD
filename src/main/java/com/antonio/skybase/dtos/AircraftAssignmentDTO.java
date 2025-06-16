package com.antonio.skybase.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AircraftAssignmentDTO {
    @NotNull(message = "Aircraft ID must not be null")
    private Integer aircraftId;

    @NotNull(message = "Flight ID must not be null")
    private Integer flightId;

    @NotNull(message = "Date must not be null")
    private LocalDate date;
}