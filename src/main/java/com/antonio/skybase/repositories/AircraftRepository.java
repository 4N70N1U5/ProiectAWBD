package com.antonio.skybase.repositories;

import com.antonio.skybase.entities.Aircraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AircraftRepository extends JpaRepository<Aircraft, Integer> {
    boolean existsByRegistration(String registration);
    Aircraft findByRegistration(String registration);
    @Query("SELECT a FROM Aircraft a WHERE a.id NOT IN (SELECT aa.id.aircraftId FROM AircraftAssignment aa WHERE aa.id.date = :date)")
    List<Aircraft> findAvailableAircraftByDate(LocalDate date);
}