package com.antonio.skybase.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "airports")
@Data
public class Airport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotBlank(message = "Airport name must not be blank")
    private String name;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Airport code must not be blank")
    @Size(min = 3, max = 3, message = "Airport code must be 3 characters")
    private String code;

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    @NotNull(message = "City must not be null")
    private City city;
}
