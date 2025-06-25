package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.EmployeeDTO;
import com.antonio.skybase.entities.Employee;
import com.antonio.skybase.entities.Job;
import com.antonio.skybase.exceptions.BadRequestException;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.services.EmployeeService;
import com.antonio.skybase.services.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmployeeMvcControllerTest {

    @Mock
    private EmployeeService employeeService;

    @Mock
    private JobService jobService;

    @InjectMocks
    private EmployeeMvcController employeeMvcController;

    private MockMvc mockMvc;

    private Employee testEmployee;
    private EmployeeDTO testEmployeeDTO;
    private List<Job> jobs;
    private List<Employee> employees;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeMvcController).build();

        // Set up test data
        Job job = new Job();
        job.setId(1);
        job.setTitle("Pilot");
        jobs = Arrays.asList(job);

        Employee manager = new Employee();
        manager.setId(2);
        manager.setFirstName("Manager");
        manager.setLastName("Smith");

        testEmployee = new Employee();
        testEmployee.setId(1);
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setPhoneNumber("123-456-7890");
        testEmployee.setEmail("john.doe@example.com");
        testEmployee.setSalary(75000);
        testEmployee.setJob(job);
        testEmployee.setFlightHours(1000);
        testEmployee.setManager(manager);

        employees = Arrays.asList(testEmployee, manager);

        testEmployeeDTO = new EmployeeDTO();
        testEmployeeDTO.setId(1);
        testEmployeeDTO.setFirstName("John");
        testEmployeeDTO.setLastName("Doe");
        testEmployeeDTO.setPhoneNumber("123-456-7890");
        testEmployeeDTO.setEmail("john.doe@example.com");
        testEmployeeDTO.setSalary(75000);
        testEmployeeDTO.setJobId(1);
        testEmployeeDTO.setFlightHours(1000);
        testEmployeeDTO.setManagerId(2);
    }

    @Test
    void testGetAllEmployees() throws Exception {
        // Given
        when(employeeService.getAll()).thenReturn(employees);

        // When & Then
        mockMvc.perform(get("/web/employees"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/list"))
                .andExpect(model().attributeExists("employees"))
                .andExpect(model().attribute("employees", employees));

        verify(employeeService).getAll();
    }

    @Test
    void testShowCreateForm() throws Exception {
        // Given
        when(jobService.getAll()).thenReturn(jobs);
        when(employeeService.getAll()).thenReturn(employees);

        // When & Then
        mockMvc.perform(get("/web/employees/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/form"))
                .andExpect(model().attributeExists("employeeDTO"))
                .andExpect(model().attributeExists("jobs"))
                .andExpect(model().attributeExists("employees"));

        verify(jobService).getAll();
        verify(employeeService).getAll();
    }

    @Test
    void testCreateEmployeeSuccess() throws Exception {
        // Given
        when(employeeService.create(any(EmployeeDTO.class))).thenReturn(testEmployee);

        // When & Then
        mockMvc.perform(post("/web/employees")
                .param("firstName", "John")
                .param("lastName", "Doe")
                .param("phoneNumber", "123-456-7890")
                .param("email", "john.doe@example.com")
                .param("salary", "75000")
                .param("jobId", "1")
                .param("flightHours", "1000")
                .param("managerId", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/employees"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(employeeService).create(any(EmployeeDTO.class));
    }

    @Test
    void testCreateEmployeeValidationErrors() throws Exception {
        // Given
        when(jobService.getAll()).thenReturn(jobs);
        when(employeeService.getAll()).thenReturn(employees);

        // When & Then - Submit form with validation errors (empty first name)
        mockMvc.perform(post("/web/employees")
                .param("firstName", "") // Empty first name
                .param("lastName", "Doe")
                .param("phoneNumber", "123-456-7890")
                .param("email", "john.doe@example.com")
                .param("salary", "75000")
                .param("jobId", "1")
                .param("flightHours", "1000"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/form"))
                .andExpect(model().attributeExists("employeeDTO"))
                .andExpect(model().attributeExists("jobs"))
                .andExpect(model().attributeExists("employees"));

        verify(jobService).getAll();
        verify(employeeService).getAll();
        verify(employeeService, never()).create(any(EmployeeDTO.class));
    }

    @Test
    void testCreateEmployeeServiceException() throws Exception {
        // Given
        when(jobService.getAll()).thenReturn(jobs);
        when(employeeService.getAll()).thenReturn(employees);
        when(employeeService.create(any(EmployeeDTO.class)))
                .thenThrow(new BadRequestException("Employee email already exists"));

        // When & Then
        mockMvc.perform(post("/web/employees")
                .param("firstName", "John")
                .param("lastName", "Doe")
                .param("phoneNumber", "123-456-7890")
                .param("email", "john.doe@example.com")
                .param("salary", "75000")
                .param("jobId", "1")
                .param("flightHours", "1000"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/form"))
                .andExpect(model().attributeExists("employeeDTO"))
                .andExpect(model().attributeExists("jobs"))
                .andExpect(model().attributeExists("employees"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(employeeService).create(any(EmployeeDTO.class));
        verify(jobService).getAll();
        verify(employeeService).getAll();
    }

    @Test
    void testGetEmployeeById() throws Exception {
        // Given
        when(employeeService.getById(1)).thenReturn(testEmployee);

        // When & Then
        mockMvc.perform(get("/web/employees/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/details"))
                .andExpect(model().attributeExists("employee"))
                .andExpect(model().attribute("employee", testEmployee));

        verify(employeeService).getById(1);
    }

    @Test
    void testGetEmployeeByIdNotFound() throws Exception {
        // Given
        when(employeeService.getById(1)).thenThrow(new NotFoundException("Employee not found"));
        when(employeeService.getAll()).thenReturn(employees);

        // When & Then
        mockMvc.perform(get("/web/employees/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/list"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attributeExists("employees"));

        verify(employeeService).getById(1);
        verify(employeeService).getAll();
    }

    @Test
    void testShowEditForm() throws Exception {
        // Given
        when(employeeService.getById(1)).thenReturn(testEmployee);
        when(jobService.getAll()).thenReturn(jobs);
        when(employeeService.getAll()).thenReturn(employees);

        // When & Then
        mockMvc.perform(get("/web/employees/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/form"))
                .andExpect(model().attributeExists("employeeDTO"))
                .andExpect(model().attributeExists("jobs"))
                .andExpect(model().attributeExists("employees"));

        verify(employeeService).getById(1);
        verify(jobService).getAll();
        verify(employeeService).getAll();
    }

    @Test
    void testUpdateEmployeeSuccess() throws Exception {
        // Given
        when(employeeService.update(eq(1), any(EmployeeDTO.class))).thenReturn(testEmployee);

        // When & Then
        mockMvc.perform(post("/web/employees/1")
                .param("firstName", "John")
                .param("lastName", "Doe")
                .param("phoneNumber", "123-456-7890")
                .param("email", "john.doe@example.com")
                .param("salary", "75000")
                .param("jobId", "1")
                .param("flightHours", "1000")
                .param("managerId", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/employees"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(employeeService).update(eq(1), any(EmployeeDTO.class));
    }

    @Test
    void testUpdateEmployeeValidationErrors() throws Exception {
        // Given
        when(jobService.getAll()).thenReturn(jobs);
        when(employeeService.getAll()).thenReturn(employees);

        // When & Then - Submit form with validation errors
        mockMvc.perform(post("/web/employees/1")
                .param("firstName", "") // Empty first name
                .param("lastName", "Doe")
                .param("phoneNumber", "123-456-7890")
                .param("email", "john.doe@example.com")
                .param("salary", "75000")
                .param("jobId", "1")
                .param("flightHours", "1000"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/form"))
                .andExpect(model().attributeExists("employeeDTO"))
                .andExpect(model().attributeExists("jobs"))
                .andExpect(model().attributeExists("employees"));

        verify(jobService).getAll();
        verify(employeeService).getAll();
        verify(employeeService, never()).update(anyInt(), any(EmployeeDTO.class));
    }

    @Test
    void testUpdateEmployeeServiceException() throws Exception {
        // Given
        when(jobService.getAll()).thenReturn(jobs);
        when(employeeService.getAll()).thenReturn(employees);
        when(employeeService.update(eq(1), any(EmployeeDTO.class)))
                .thenThrow(new BadRequestException("Invalid employee data"));

        // When & Then
        mockMvc.perform(post("/web/employees/1")
                .param("firstName", "John")
                .param("lastName", "Doe")
                .param("phoneNumber", "123-456-7890")
                .param("email", "john.doe@example.com")
                .param("salary", "75000")
                .param("jobId", "1")
                .param("flightHours", "1000"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/form"))
                .andExpect(model().attributeExists("employeeDTO"))
                .andExpect(model().attributeExists("jobs"))
                .andExpect(model().attributeExists("employees"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(employeeService).update(eq(1), any(EmployeeDTO.class));
        verify(jobService).getAll();
        verify(employeeService).getAll();
    }

    @Test
    void testDeleteEmployeeSuccess() throws Exception {
        // Given
        doNothing().when(employeeService).delete(1);

        // When & Then
        mockMvc.perform(post("/web/employees/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/employees"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(employeeService).delete(1);
    }

    @Test
    void testDeleteEmployeeServiceException() throws Exception {
        // Given
        doThrow(new BadRequestException("Cannot delete employee with existing assignments")).when(employeeService)
                .delete(1);

        // When & Then
        mockMvc.perform(post("/web/employees/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/employees"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(employeeService).delete(1);
    }
}
