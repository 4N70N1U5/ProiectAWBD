package com.antonio.skybase.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Entity
@Table(name = "employees")
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "First name must not be blank")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NotBlank(message = "Last name must not be blank")
    private String lastName;

    @Column(name = "phone_number", nullable = false)
    @NotBlank(message = "Phone number must not be blank")
    private String phoneNumber;

    @Column(nullable = false)
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email must not be blank")
    private String email;

    @Column(nullable = false)
    @NotNull(message = "Salary must not be null")
    @Positive(message = "Salary must be positive")
    private Integer salary;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    @NotNull(message = "Job must not be null")
    private Job job;

    @Column(name = "flight_hours")
    @Positive(message = "Flight hours must be positive")
    private Integer flightHours;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Employee manager;
}
