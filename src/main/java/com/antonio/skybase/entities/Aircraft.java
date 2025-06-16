package com.antonio.skybase.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Entity
@Table(name = "aircraft")
@Data
public class Aircraft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Aircraft registration must not be blank")
    private String registration;

    @Column(nullable = false)
    @NotBlank(message = "Aircraft type must not be blank")
    private String type;

    @Column(name = "`range`")
    @Positive(message = "Range must be positive")
    private Integer range;

    @Column
    @Positive(message = "Capacity must be positive")
    private Integer capacity;
}
