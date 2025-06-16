package com.antonio.skybase.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class JobDTO {
    private Integer id;

    @NotBlank(message = "Job title must not be blank")
    private String title;

    @NotNull(message = "Minimum salary must not be null")
    @Positive(message = "Maximum salary must be positive")
    private Double minSalary;

    @NotNull(message = "Maximum salary must not be null")
    @Positive(message = "Maximum salary must be positive")
    private Double maxSalary;

    @NotNull(message = "Department ID must not be null")
    private Integer departmentId;
}