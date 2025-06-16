package com.antonio.skybase.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Data
public class AircraftAssignmentId implements Serializable {
    @Column(name = "aircraft_id", nullable = false)
    @NotNull(message = "Aircraft ID must not be null")
    private Integer aircraftId;

    @Column(name = "flight_id", nullable = false)
    @NotNull(message = "Flight ID must not be null")
    private Integer flightId;

    @Column(name = "date", nullable = false)
    @NotNull(message = "Date must not be null")
    private LocalDate date;
}