package com.antonio.skybase.services;

import com.antonio.skybase.dtos.EmployeeDTO;
import com.antonio.skybase.entities.Employee;
import com.antonio.skybase.entities.Job;
import com.antonio.skybase.exceptions.BadRequestException;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.repositories.EmployeeRepository;
import com.antonio.skybase.repositories.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateEmployee() {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setFirstName("John");
        employeeDTO.setLastName("Doe");
        employeeDTO.setPhoneNumber("1234567890");
        employeeDTO.setEmail("john.doe@example.com");
        employeeDTO.setSalary(50000);
        employeeDTO.setJobId(1);

        Job job = new Job();
        job.setId(1);
        job.setTitle("Test Job");

        Employee employee = new Employee();
        employee.setId(1);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setPhoneNumber("1234567890");
        employee.setEmail("john.doe@example.com");
        employee.setSalary(50000);
        employee.setJob(job);

        when(jobRepository.findById(anyInt())).thenReturn(Optional.of(job));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        Employee createdEmployee = employeeService.create(employeeDTO);

        assertNotNull(createdEmployee);
        assertEquals("John", createdEmployee.getFirstName());
        assertEquals("Doe", createdEmployee.getLastName());
        assertEquals("1234567890", createdEmployee.getPhoneNumber());
        assertEquals("john.doe@example.com", createdEmployee.getEmail());
        assertEquals(50000, createdEmployee.getSalary());
        assertEquals(job, createdEmployee.getJob());
    }

    @Test
    void testCreateEmployeeWithInvalidJob() {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setFirstName("John");
        employeeDTO.setLastName("Doe");
        employeeDTO.setPhoneNumber("1234567890");
        employeeDTO.setEmail("john.doe@example.com");
        employeeDTO.setSalary(50000);
        employeeDTO.setJobId(1);

        when(jobRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> employeeService.create(employeeDTO));
    }

    @Test
    void testGetAllEmployees() {
        Job job = new Job();
        job.setId(1);
        job.setTitle("Test Job");

        Employee employee1 = new Employee();
        employee1.setId(1);
        employee1.setFirstName("John");
        employee1.setLastName("Doe");
        employee1.setPhoneNumber("1234567890");
        employee1.setEmail("john.doe@example.com");
        employee1.setSalary(50000);
        employee1.setJob(job);

        Employee employee2 = new Employee();
        employee2.setId(2);
        employee2.setFirstName("Jane");
        employee2.setLastName("Doe");
        employee2.setPhoneNumber("0987654321");
        employee2.setEmail("jane.doe@example.com");
        employee2.setSalary(60000);
        employee2.setJob(job);

        List<Employee> employees = Arrays.asList(employee1, employee2);

        when(employeeRepository.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.getAll();

        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Jane", result.get(1).getFirstName());
    }

    @Test
    void testGetEmployeeById() {
        Job job = new Job();
        job.setId(1);
        job.setTitle("Test Job");

        Employee employee = new Employee();
        employee.setId(1);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setPhoneNumber("1234567890");
        employee.setEmail("john.doe@example.com");
        employee.setSalary(50000);
        employee.setJob(job);

        when(employeeRepository.findById(anyInt())).thenReturn(Optional.of(employee));

        Employee result = employeeService.getById(1);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
    }

    @Test
    void testGetEmployeeByIdNotFound() {
        when(employeeRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> employeeService.getById(1));
    }

    @Test
    void testUpdateEmployee() {
        Job job = new Job();
        job.setId(1);
        job.setTitle("Test Job");

        Employee employee = new Employee();
        employee.setId(1);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setPhoneNumber("1234567890");
        employee.setEmail("john.doe@example.com");
        employee.setSalary(50000);
        employee.setJob(job);

        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setFirstName("Jane");
        employeeDTO.setLastName("Doe");
        employeeDTO.setPhoneNumber("0987654321");
        employeeDTO.setEmail("jane.doe@example.com");
        employeeDTO.setSalary(60000);
        employeeDTO.setJobId(1);

        when(employeeRepository.findById(anyInt())).thenReturn(Optional.of(employee));
        when(jobRepository.findById(anyInt())).thenReturn(Optional.of(job));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        Employee updatedEmployee = employeeService.update(1, employeeDTO);

        assertNotNull(updatedEmployee);
        assertEquals("Jane", updatedEmployee.getFirstName());
        assertEquals("Doe", updatedEmployee.getLastName());
        assertEquals("0987654321", updatedEmployee.getPhoneNumber());
        assertEquals("jane.doe@example.com", updatedEmployee.getEmail());
        assertEquals(60000, updatedEmployee.getSalary());
        assertEquals(job, updatedEmployee.getJob());
    }

    @Test
    void testUpdateEmployeeWithInvalidJob() {
        Employee employee = new Employee();
        employee.setId(1);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setPhoneNumber("1234567890");
        employee.setEmail("john.doe@example.com");
        employee.setSalary(50000);

        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setFirstName("Jane");
        employeeDTO.setLastName("Doe");
        employeeDTO.setPhoneNumber("0987654321");
        employeeDTO.setEmail("jane.doe@example.com");
        employeeDTO.setSalary(60000);
        employeeDTO.setJobId(1);

        when(employeeRepository.findById(anyInt())).thenReturn(Optional.of(employee));
        when(jobRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> employeeService.update(1, employeeDTO));
    }

    @Test
    void testDeleteEmployee() {
        doNothing().when(employeeRepository).deleteById(anyInt());

        employeeService.delete(1);

        verify(employeeRepository, times(1)).deleteById(1);
    }
}