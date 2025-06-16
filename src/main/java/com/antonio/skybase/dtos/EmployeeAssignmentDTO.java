package com.antonio.skybase.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeAssignmentDTO {
    @NotNull(message = "Employee ID must not be null")
    private Integer employeeId;

    @NotNull(message = "Flight ID must not be null")
    private Integer flightId;

    @NotNull(message = "Date must not be null")
    private LocalDate date;
}