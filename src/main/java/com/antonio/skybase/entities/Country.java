package com.antonio.skybase.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "countries")
@Data
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotBlank(message = "Country name must not be blank")
    private String name;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Country code must not be blank")
    @Size(min = 2, max = 2, message = "Country code must be 2 characters")
    private String code;
}
