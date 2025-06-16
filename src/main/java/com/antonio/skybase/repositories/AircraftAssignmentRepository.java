package com.antonio.skybase.repositories;

import com.antonio.skybase.entities.AircraftAssignment;
import com.antonio.skybase.entities.AircraftAssignmentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AircraftAssignmentRepository extends JpaRepository<AircraftAssignment, AircraftAssignmentId> {
    List<AircraftAssignment> findByIdDate(LocalDate date);
    List<AircraftAssignment> findByIdAircraftIdAndIdDateBetween(Integer aircraftId, LocalDate startDate, LocalDate endDate);
    List<AircraftAssignment> findByIdFlightIdAndIdDateBetween(Integer flightId, LocalDate startDate, LocalDate endDate);
}