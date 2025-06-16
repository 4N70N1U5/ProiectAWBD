package com.antonio.skybase.services;

import com.antonio.skybase.dtos.EmployeeAssignmentDTO;
import com.antonio.skybase.entities.*;
import com.antonio.skybase.exceptions.BadRequestException;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.repositories.EmployeeAssignmentRepository;
import com.antonio.skybase.repositories.EmployeeRepository;
import com.antonio.skybase.repositories.FlightRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeeAssignmentService {
    @Autowired
    private EmployeeAssignmentRepository employeeAssignmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private FlightRepository flightRepository;

    public EmployeeAssignment create(EmployeeAssignmentDTO employeeAssignmentDTO) {
        Employee employee = employeeRepository.findById(employeeAssignmentDTO.getEmployeeId())
                .orElseThrow(() -> new NotFoundException("Employee with ID " + employeeAssignmentDTO.getEmployeeId() + " not found"));
        Flight flight = flightRepository.findById(employeeAssignmentDTO.getFlightId())
                .orElseThrow(() -> new NotFoundException("Flight with ID " + employeeAssignmentDTO.getFlightId() + " not found"));

        validateEmployeeAvailability(employeeAssignmentDTO.getEmployeeId(), employeeAssignmentDTO.getDate());
        validateEmployeeJob(employee.getJob());

        EmployeeAssignmentId id = new EmployeeAssignmentId();
        id.setEmployeeId(employeeAssignmentDTO.getEmployeeId());
        id.setFlightId(employeeAssignmentDTO.getFlightId());
        id.setDate(employeeAssignmentDTO.getDate());

        EmployeeAssignment employeeAssignment = new EmployeeAssignment();
        employeeAssignment.setId(id);
        employeeAssignment.setEmployee(employee);
        employeeAssignment.setFlight(flight);

        return employeeAssignmentRepository.save(employeeAssignment);
    }

    public List<EmployeeAssignment> getAll() {
        return employeeAssignmentRepository.findAll();
    }

    public EmployeeAssignment getById(EmployeeAssignmentId id) {
        return employeeAssignmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("EmployeeAssignment with ID " + id + " not found"));
    }

    public List<EmployeeAssignment> getAssignmentsByDate(LocalDate date) {
        return employeeAssignmentRepository.findByIdDate(date);
    }

    public List<EmployeeAssignment> getAssignmentsByEmployeeAndDateRange(Integer employeeId, LocalDate startDate, LocalDate endDate) {
        return employeeAssignmentRepository.findByIdEmployeeIdAndIdDateBetween(employeeId, startDate, endDate);
    }

    public List<EmployeeAssignment> getAssignmentsByFlightAndDateRange(Integer flightId, LocalDate startDate, LocalDate endDate) {
        return employeeAssignmentRepository.findByIdFlightIdAndIdDateBetween(flightId, startDate, endDate);
    }

    public EmployeeAssignment update(EmployeeAssignmentId id, @Valid EmployeeAssignmentDTO employeeAssignmentDTO) {
        EmployeeAssignment employeeAssignment = employeeAssignmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("EmployeeAssignment with ID " + id + " not found"));

        Employee employee = employeeRepository.findById(employeeAssignmentDTO.getEmployeeId())
                .orElseThrow(() -> new NotFoundException("Employee with ID " + employeeAssignmentDTO.getEmployeeId() + " not found"));
        Flight flight = flightRepository.findById(employeeAssignmentDTO.getFlightId())
                .orElseThrow(() -> new NotFoundException("Flight with ID " + employeeAssignmentDTO.getFlightId() + " not found"));

        if (!employeeAssignment.getId().getEmployeeId().equals(employeeAssignmentDTO.getEmployeeId())) {
            validateEmployeeAvailability(employeeAssignmentDTO.getEmployeeId(), employeeAssignmentDTO.getDate());
        }
        validateEmployeeJob(employee.getJob());

        EmployeeAssignmentId newId = new EmployeeAssignmentId();
        newId.setEmployeeId(employeeAssignmentDTO.getEmployeeId());
        newId.setFlightId(employeeAssignmentDTO.getFlightId());
        newId.setDate(employeeAssignmentDTO.getDate());

        employeeAssignment.setId(newId);
        employeeAssignment.setEmployee(employee);
        employeeAssignment.setFlight(flight);

        employeeAssignmentRepository.deleteById(id);
        return employeeAssignmentRepository.save(employeeAssignment);
    }

    public void delete(EmployeeAssignmentId id) {
        employeeAssignmentRepository.deleteById(id);
    }

    private void validateEmployeeAvailability(Integer employeeId, LocalDate date) {
        List<EmployeeAssignment> assignments = employeeAssignmentRepository.findByIdEmployeeIdAndIdDateBetween(employeeId, date, date);
        if (!assignments.isEmpty()) {
            throw new BadRequestException("Employee with ID " + employeeId + " is not available on " + date);
        }
    }

    private void validateEmployeeJob(Job job) {
        if (!(
                job.getDepartment().getId().equals(1) // Echipaj de zbor
                || job.getDepartment().getId().equals(2) // Personal de cabina
        )) {
            throw new BadRequestException("Employee with job " + job.getTitle() + " cannot be assigned to a flight");
        }
    }
}