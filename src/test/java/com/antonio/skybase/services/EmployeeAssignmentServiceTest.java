package com.antonio.skybase.services;

import com.antonio.skybase.dtos.EmployeeAssignmentDTO;
import com.antonio.skybase.entities.*;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.repositories.EmployeeAssignmentRepository;
import com.antonio.skybase.repositories.EmployeeRepository;
import com.antonio.skybase.repositories.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class EmployeeAssignmentServiceTest {

    @Mock
    private EmployeeAssignmentRepository employeeAssignmentRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private EmployeeAssignmentService employeeAssignmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateEmployeeAssignment() {
        EmployeeAssignmentDTO employeeAssignmentDTO = new EmployeeAssignmentDTO();
        employeeAssignmentDTO.setEmployeeId(1);
        employeeAssignmentDTO.setFlightId(1);
        employeeAssignmentDTO.setDate(LocalDate.of(2023, 10, 10));

        Department department = new Department();
        department.setId(1);
        department.setName("Flight Crew");

        Job job = new Job();
        job.setId(1);
        job.setTitle("Captain");
        job.setDepartment(department);

        Employee employee = new Employee();
        employee.setId(1);
        employee.setJob(job);

        Flight flight = new Flight();
        flight.setId(1);

        EmployeeAssignmentId id = new EmployeeAssignmentId();
        id.setEmployeeId(1);
        id.setFlightId(1);
        id.setDate(LocalDate.of(2023, 10, 10));

        EmployeeAssignment employeeAssignment = new EmployeeAssignment();
        employeeAssignment.setId(id);
        employeeAssignment.setEmployee(employee);
        employeeAssignment.setFlight(flight);

        when(employeeRepository.findById(anyInt())).thenReturn(Optional.of(employee));
        when(flightRepository.findById(anyInt())).thenReturn(Optional.of(flight));
        when(employeeAssignmentRepository.save(any(EmployeeAssignment.class))).thenReturn(employeeAssignment);

        EmployeeAssignment createdEmployeeAssignment = employeeAssignmentService.create(employeeAssignmentDTO);

        assertNotNull(createdEmployeeAssignment);
        assertEquals(1, createdEmployeeAssignment.getId().getEmployeeId());
        assertEquals(1, createdEmployeeAssignment.getId().getFlightId());
        assertEquals(LocalDate.of(2023, 10, 10), createdEmployeeAssignment.getId().getDate());
    }

    @Test
    void testCreateEmployeeAssignmentWithInvalidEmployee() {
        EmployeeAssignmentDTO employeeAssignmentDTO = new EmployeeAssignmentDTO();
        employeeAssignmentDTO.setEmployeeId(1);
        employeeAssignmentDTO.setFlightId(1);
        employeeAssignmentDTO.setDate(LocalDate.of(2023, 10, 10));

        when(employeeRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> employeeAssignmentService.create(employeeAssignmentDTO));
    }

    @Test
    void testGetAllEmployeeAssignments() {
        EmployeeAssignmentId id1 = new EmployeeAssignmentId();
        id1.setEmployeeId(1);
        id1.setFlightId(1);
        id1.setDate(LocalDate.of(2023, 10, 10));

        EmployeeAssignmentId id2 = new EmployeeAssignmentId();
        id2.setEmployeeId(2);
        id2.setFlightId(2);
        id2.setDate(LocalDate.of(2023, 10, 11));

        EmployeeAssignment employeeAssignment1 = new EmployeeAssignment();
        employeeAssignment1.setId(id1);

        EmployeeAssignment employeeAssignment2 = new EmployeeAssignment();
        employeeAssignment2.setId(id2);

        List<EmployeeAssignment> employeeAssignments = Arrays.asList(employeeAssignment1, employeeAssignment2);

        when(employeeAssignmentRepository.findAll()).thenReturn(employeeAssignments);

        List<EmployeeAssignment> result = employeeAssignmentService.getAll();

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId().getEmployeeId());
        assertEquals(2, result.get(1).getId().getEmployeeId());
    }

    @Test
    void testGetEmployeeAssignmentById() {
        EmployeeAssignmentId id = new EmployeeAssignmentId();
        id.setEmployeeId(1);
        id.setFlightId(1);
        id.setDate(LocalDate.of(2023, 10, 10));

        EmployeeAssignment employeeAssignment = new EmployeeAssignment();
        employeeAssignment.setId(id);

        when(employeeAssignmentRepository.findById(any(EmployeeAssignmentId.class))).thenReturn(Optional.of(employeeAssignment));

        EmployeeAssignment result = employeeAssignmentService.getById(id);

        assertNotNull(result);
        assertEquals(1, result.getId().getEmployeeId());
        assertEquals(1, result.getId().getFlightId());
        assertEquals(LocalDate.of(2023, 10, 10), result.getId().getDate());
    }

    @Test
    void testGetEmployeeAssignmentByIdNotFound() {
        EmployeeAssignmentId id = new EmployeeAssignmentId();
        id.setEmployeeId(1);
        id.setFlightId(1);
        id.setDate(LocalDate.of(2023, 10, 10));

        when(employeeAssignmentRepository.findById(any(EmployeeAssignmentId.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> employeeAssignmentService.getById(id));
    }

    @Test
    void testUpdateEmployeeAssignment() {
        EmployeeAssignmentId id = new EmployeeAssignmentId();
        id.setEmployeeId(1);
        id.setFlightId(1);
        id.setDate(LocalDate.of(2023, 10, 10));

        EmployeeAssignmentDTO employeeAssignmentDTO = new EmployeeAssignmentDTO();
        employeeAssignmentDTO.setEmployeeId(1);
        employeeAssignmentDTO.setFlightId(1);
        employeeAssignmentDTO.setDate(LocalDate.of(2023, 10, 10));

        Department department = new Department();
        department.setId(1);
        department.setName("Flight Crew");

        Job job = new Job();
        job.setId(1);
        job.setTitle("Captain");
        job.setDepartment(department);

        Employee employee = new Employee();
        employee.setId(1);
        employee.setJob(job);

        Flight flight = new Flight();
        flight.setId(1);

        EmployeeAssignment employeeAssignment = new EmployeeAssignment();
        employeeAssignment.setId(id);
        employeeAssignment.setEmployee(employee);
        employeeAssignment.setFlight(flight);

        when(employeeAssignmentRepository.findById(any(EmployeeAssignmentId.class))).thenReturn(Optional.of(employeeAssignment));
        when(employeeRepository.findById(anyInt())).thenReturn(Optional.of(employee));
        when(flightRepository.findById(anyInt())).thenReturn(Optional.of(flight));
        when(employeeAssignmentRepository.save(any(EmployeeAssignment.class))).thenReturn(employeeAssignment);

        EmployeeAssignment updatedEmployeeAssignment = employeeAssignmentService.update(id, employeeAssignmentDTO);

        assertNotNull(updatedEmployeeAssignment);
        assertEquals(1, updatedEmployeeAssignment.getId().getEmployeeId());
        assertEquals(1, updatedEmployeeAssignment.getId().getFlightId());
        assertEquals(LocalDate.of(2023, 10, 10), updatedEmployeeAssignment.getId().getDate());
    }

    @Test
    void testUpdateEmployeeAssignmentWithInvalidEmployee() {
        EmployeeAssignmentId id = new EmployeeAssignmentId();
        id.setEmployeeId(1);
        id.setFlightId(1);
        id.setDate(LocalDate.of(2023, 10, 10));

        EmployeeAssignmentDTO employeeAssignmentDTO = new EmployeeAssignmentDTO();
        employeeAssignmentDTO.setEmployeeId(1);
        employeeAssignmentDTO.setFlightId(1);
        employeeAssignmentDTO.setDate(LocalDate.of(2023, 10, 10));

        when(employeeAssignmentRepository.findById(any(EmployeeAssignmentId.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> employeeAssignmentService.update(id, employeeAssignmentDTO));
    }

    @Test
    void testDeleteEmployeeAssignment() {
        EmployeeAssignmentId id = new EmployeeAssignmentId();
        id.setEmployeeId(1);
        id.setFlightId(1);
        id.setDate(LocalDate.of(2023, 10, 10));

        doNothing().when(employeeAssignmentRepository).deleteById(any(EmployeeAssignmentId.class));

        employeeAssignmentService.delete(id);

        verify(employeeAssignmentRepository, times(1)).deleteById(id);
    }
}