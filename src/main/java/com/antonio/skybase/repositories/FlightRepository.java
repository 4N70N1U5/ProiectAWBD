package com.antonio.skybase.repositories;

import com.antonio.skybase.entities.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<Flight, Integer> {
    boolean existsByNumber(String number);
    Flight findByNumber(String number);
}