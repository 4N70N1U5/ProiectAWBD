package com.antonio.skybase.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "aircraft_assignments")
@Data
public class AircraftAssignment {
    @EmbeddedId
    private AircraftAssignmentId id;

    @ManyToOne
    @MapsId("aircraftId")
    @JoinColumn(name = "aircraft_id", nullable = false)
    @NotNull(message = "Aircraft must not be null")
    private Aircraft aircraft;

    @ManyToOne
    @MapsId("flightId")
    @JoinColumn(name = "flight_id", nullable = false)
    @NotNull(message = "Flight must not be null")
    private Flight flight;
}