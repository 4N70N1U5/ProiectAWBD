package com.antonio.skybase.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "cities")
@Data
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotBlank(message = "City name must not be blank")
    private String name;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    @NotNull(message = "Country must not be null")
    private Country country;
}
