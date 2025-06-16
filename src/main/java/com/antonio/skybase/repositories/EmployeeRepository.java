package com.antonio.skybase.repositories;

import com.antonio.skybase.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    @Query("SELECT e FROM Employee e WHERE e.id NOT IN (SELECT ea.id.employeeId FROM EmployeeAssignment ea WHERE ea.id.date = :date)")
    List<Employee> findAvailableEmployeesByDate(LocalDate date);
}