package com.antonio.skybase.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class EmployeeDTO {
    private Integer id;

    @NotBlank(message = "First name must not be blank")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    private String lastName;

    @NotBlank(message = "Phone number must not be blank")
    private String phoneNumber;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email must not be blank")
    private String email;

    @NotNull(message = "Salary must not be null")
    @Positive(message = "Salary must be positive")
    private Integer salary;

    @NotNull(message = "Job ID must not be null")
    private Integer jobId;

    @Positive(message = "Flight hours must be positive")
    private Integer flightHours;

    private Integer managerId;
}