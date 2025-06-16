package com.antonio.skybase.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AirportDTO {
    private Integer id;

    @NotBlank(message = "Airport name must not be blank")
    private String name;

    @NotBlank(message = "Airport code must not be blank")
    @Size(min = 3, max = 3, message = "Airport code must be 3 characters")
    private String code;

    @NotNull(message = "City ID must not be null")
    private Integer cityId;
}