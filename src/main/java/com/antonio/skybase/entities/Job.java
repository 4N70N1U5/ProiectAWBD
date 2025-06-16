package com.antonio.skybase.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Entity
@Table(name = "jobs")
@Data
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotBlank(message = "Job title must not be blank")
    private String title;

    @Column(name = "min_salary")
    @NotNull(message = "Minimum salary must not be null")
    @Positive(message = "Minimum salary must be positive")
    private Double minSalary;

    @Column(name = "max_salary")
    @NotNull(message = "Maximum salary must not be null")
    @Positive(message = "Maximum salary must be positive")
    private Double maxSalary;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    @NotNull(message = "Department must not be null")
    private Department department;
}
