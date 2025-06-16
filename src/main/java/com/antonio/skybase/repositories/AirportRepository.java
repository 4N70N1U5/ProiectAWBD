package com.antonio.skybase.repositories;

import com.antonio.skybase.entities.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirportRepository extends JpaRepository<Airport, Integer> {
    boolean existsByCode(String code);
    Airport findByCode(String code);
}