package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.EmployeeDTO;
import com.antonio.skybase.entities.Employee;
import com.antonio.skybase.services.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EmployeeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
    }

    @Test
    void testCreateEmployee() throws Exception {
        Employee employee = new Employee();
        employee.setId(1);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setPhoneNumber("1234567890");
        employee.setEmail("john.doe@example.com");
        employee.setSalary(50000);

        when(employeeService.create(any(EmployeeDTO.class))).thenReturn(employee);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"John\", \"lastName\": \"Doe\", \"phoneNumber\": \"1234567890\", \"email\": \"john.doe@example.com\", \"salary\": 50000, \"jobId\": 1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.phoneNumber").value("1234567890"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.salary").value(50000));
    }

    @Test
    void testCreateEmployeeWithBlankFirstName() throws Exception {
        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"\", \"lastName\": \"Doe\", \"phoneNumber\": \"1234567890\", \"email\": \"john.doe@example.com\", \"salary\": 50000, \"jobId\": 1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateEmployeeWithBlankLastName() throws Exception {
        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"John\", \"lastName\": \"\", \"phoneNumber\": \"1234567890\", \"email\": \"john.doe@example.com\", \"salary\": 50000, \"jobId\": 1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateEmployeeWithInvalidEmail() throws Exception {
        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"John\", \"lastName\": \"Doe\", \"phoneNumber\": \"1234567890\", \"email\": \"invalid-email\", \"salary\": 50000, \"jobId\": 1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateEmployeeWithNegativeSalary() throws Exception {
        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"John\", \"lastName\": \"Doe\", \"phoneNumber\": \"1234567890\", \"email\": \"john.doe@example.com\", \"salary\": -50000, \"jobId\": 1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllEmployees() throws Exception {
        Employee employee1 = new Employee();
        employee1.setId(1);
        employee1.setFirstName("John");
        employee1.setLastName("Doe");
        employee1.setPhoneNumber("1234567890");
        employee1.setEmail("john.doe@example.com");
        employee1.setSalary(50000);

        Employee employee2 = new Employee();
        employee2.setId(2);
        employee2.setFirstName("Jane");
        employee2.setLastName("Doe");
        employee2.setPhoneNumber("0987654321");
        employee2.setEmail("jane.doe@example.com");
        employee2.setSalary(60000);

        when(employeeService.getAll()).thenReturn(List.of(employee1, employee2));

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].phoneNumber").value("1234567890"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$[0].salary").value(50000))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[1].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].phoneNumber").value("0987654321"))
                .andExpect(jsonPath("$[1].email").value("jane.doe@example.com"))
                .andExpect(jsonPath("$[1].salary").value(60000));
    }

    @Test
    void testGetEmployeeById() throws Exception {
        Employee employee = new Employee();
        employee.setId(1);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setPhoneNumber("1234567890");
        employee.setEmail("john.doe@example.com");
        employee.setSalary(50000);

        when(employeeService.getById(anyInt())).thenReturn(employee);

        mockMvc.perform(get("/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.phoneNumber").value("1234567890"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.salary").value(50000));
    }

    @Test
    void testUpdateEmployee() throws Exception {
        Employee employee = new Employee();
        employee.setId(1);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setPhoneNumber("1234567890");
        employee.setEmail("john.doe@example.com");
        employee.setSalary(50000);

        when(employeeService.update(anyInt(), any(EmployeeDTO.class))).thenReturn(employee);

        mockMvc.perform(put("/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"John\", \"lastName\": \"Doe\", \"phoneNumber\": \"1234567890\", \"email\": \"john.doe@example.com\", \"salary\": 50000, \"jobId\": 1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.phoneNumber").value("1234567890"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.salary").value(50000));
    }

    @Test
    void testDeleteEmployee() throws Exception {
        mockMvc.perform(delete("/employees/1"))
                .andExpect(status().isNoContent());
    }
}