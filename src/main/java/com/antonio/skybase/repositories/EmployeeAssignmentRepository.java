package com.antonio.skybase.repositories;

import com.antonio.skybase.entities.EmployeeAssignment;
import com.antonio.skybase.entities.EmployeeAssignmentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeAssignmentRepository extends JpaRepository<EmployeeAssignment, EmployeeAssignmentId> {
    List<EmployeeAssignment> findByIdDate(LocalDate date);
    List<EmployeeAssignment> findByIdEmployeeIdAndIdDateBetween(Integer employeeId, LocalDate startDate, LocalDate endDate);
    List<EmployeeAssignment> findByIdFlightIdAndIdDateBetween(Integer flightId, LocalDate startDate, LocalDate endDate);
}