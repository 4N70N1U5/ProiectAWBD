package com.antonio.skybase.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "employee_assignments")
@Data
public class EmployeeAssignment {
    @EmbeddedId
    private EmployeeAssignmentId id;

    @ManyToOne
    @MapsId("employeeId")
    @JoinColumn(name = "employee_id", nullable = false)
    @NotNull(message = "Employee must not be null")
    private Employee employee;

    @ManyToOne
    @MapsId("flightId")
    @JoinColumn(name = "flight_id", nullable = false)
    @NotNull(message = "Flight must not be null")
    private Flight flight;
}