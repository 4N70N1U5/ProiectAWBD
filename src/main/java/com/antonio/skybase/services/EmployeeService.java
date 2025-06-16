package com.antonio.skybase.services;

import com.antonio.skybase.dtos.EmployeeDTO;
import com.antonio.skybase.entities.Employee;
import com.antonio.skybase.entities.EmployeeAssignment;
import com.antonio.skybase.entities.Job;
import com.antonio.skybase.exceptions.BadRequestException;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.repositories.EmployeeAssignmentRepository;
import com.antonio.skybase.repositories.EmployeeRepository;
import com.antonio.skybase.repositories.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private EmployeeAssignmentRepository employeeAssignmentRepository;

    public Employee create(EmployeeDTO employeeDTO) {
        Job job = validateJobExists(employeeDTO.getJobId());
        Employee manager = employeeDTO.getManagerId() != null ? validateEmployeeExists(employeeDTO.getManagerId()) : null;

        Employee employee = new Employee();
        employee.setFirstName(employeeDTO.getFirstName());
        employee.setLastName(employeeDTO.getLastName());
        employee.setPhoneNumber(employeeDTO.getPhoneNumber());
        employee.setEmail(employeeDTO.getEmail());
        employee.setSalary(employeeDTO.getSalary());
        employee.setJob(job);
        employee.setFlightHours(employeeDTO.getFlightHours());
        employee.setManager(manager);

        return employeeRepository.save(employee);
    }

    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    public Employee getById(Integer id) {
        return employeeRepository.findById(id).orElseThrow(() -> new NotFoundException("Employee with ID " + id + " not found"));
    }

    public List<Employee> getAvailableEmployeesByDate(LocalDate date) {
        return employeeRepository.findAvailableEmployeesByDate(date);
    }

    public List<LocalDate> getEmployeeAvailabilities(Integer id, LocalDate startDate, LocalDate endDate) {
        List<EmployeeAssignment> employeeAssignments = employeeAssignmentRepository.findByIdEmployeeIdAndIdDateBetween(id, startDate, endDate);

        List<LocalDate> assignedDates = employeeAssignments.stream()
                .map(assignment -> assignment.getId().getDate())
                .toList();

        return Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(startDate, endDate) + 1)
                .filter(date -> !assignedDates.contains(date))
                .collect(Collectors.toList());
    }

    public Employee update(Integer id, EmployeeDTO employeeDTO) {
        Employee employeeToUpdate = employeeRepository.findById(id).orElseThrow(() -> new NotFoundException("Employee with ID " + id + " not found"));
        Job job = validateJobExists(employeeDTO.getJobId());
        Employee manager = employeeDTO.getManagerId() != null ? validateEmployeeExists(employeeDTO.getManagerId()) : null;

        employeeToUpdate.setFirstName(employeeDTO.getFirstName());
        employeeToUpdate.setLastName(employeeDTO.getLastName());
        employeeToUpdate.setPhoneNumber(employeeDTO.getPhoneNumber());
        employeeToUpdate.setEmail(employeeDTO.getEmail());
        employeeToUpdate.setSalary(employeeDTO.getSalary());
        employeeToUpdate.setJob(job);
        employeeToUpdate.setFlightHours(employeeDTO.getFlightHours());
        employeeToUpdate.setManager(manager);

        return employeeRepository.save(employeeToUpdate);
    }

    public void delete(Integer id) {
        employeeRepository.deleteById(id);
    }

    private Job validateJobExists(Integer jobId) {
        return jobRepository.findById(jobId).orElseThrow(() -> new BadRequestException("Job with ID " + jobId + " does not exist"));
    }

    private Employee validateEmployeeExists(Integer employeeId) {
        return employeeRepository.findById(employeeId).orElseThrow(() -> new BadRequestException("Employee with ID " + employeeId + " does not exist"));
    }
}