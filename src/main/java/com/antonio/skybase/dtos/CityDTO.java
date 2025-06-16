package com.antonio.skybase.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CityDTO {
    private Integer id;

    @NotBlank(message = "City name must not be blank")
    private String name;

    @NotNull(message = "Country ID must not be null")
    private Integer countryId;
}