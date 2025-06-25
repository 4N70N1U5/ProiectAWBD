package com.antonio.skybase.controllers;

import com.antonio.skybase.config.TestConfig;
import com.antonio.skybase.entities.Department;
import com.antonio.skybase.entities.Employee;
import com.antonio.skybase.entities.Job;
import com.antonio.skybase.repositories.DepartmentRepository;
import com.antonio.skybase.repositories.EmployeeRepository;
import com.antonio.skybase.repositories.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(TestConfig.class)
@ActiveProfiles("test")
@Transactional
class EmployeeMvcControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private MockMvc mockMvc;
    private Job testJob;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Clean up existing data
        employeeRepository.deleteAll();
        jobRepository.deleteAll();
        departmentRepository.deleteAll();

        // Setup test data
        Department department = new Department();
        department.setName("Test Department");
        department = departmentRepository.save(department);

        testJob = new Job();
        testJob.setTitle("Test Job");
        testJob.setMinSalary(40000.0);
        testJob.setMaxSalary(80000.0);
        testJob.setDepartment(department);
        testJob = jobRepository.save(testJob);
    }

    @Test
    void testShowEmployeeList() throws Exception {
        // Given
        Employee emp1 = new Employee();
        emp1.setFirstName("John");
        emp1.setLastName("Doe");
        emp1.setPhoneNumber("1234567890");
        emp1.setEmail("john.doe@example.com");
        emp1.setSalary(50000);
        emp1.setJob(testJob);
        employeeRepository.save(emp1);

        Employee emp2 = new Employee();
        emp2.setFirstName("Jane");
        emp2.setLastName("Smith");
        emp2.setPhoneNumber("0987654321");
        emp2.setEmail("jane.smith@example.com");
        emp2.setSalary(55000);
        emp2.setJob(testJob);
        employeeRepository.save(emp2);

        // When & Then
        mockMvc.perform(get("/web/employees"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/list"))
                .andExpect(model().attributeExists("employees"));
    }

    @Test
    void testShowAddEmployeeForm() throws Exception {
        // When & Then
        mockMvc.perform(get("/web/employees/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/form"))
                .andExpect(model().attributeExists("employeeDTO"))
                .andExpect(model().attributeExists("jobs"))
                .andExpect(model().attributeExists("employees"));
    }

    @Test
    void testShowEditEmployeeForm() throws Exception {
        // Given
        Employee employee = new Employee();
        employee.setFirstName("Alice");
        employee.setLastName("Johnson");
        employee.setPhoneNumber("1111111111");
        employee.setEmail("alice@example.com");
        employee.setSalary(45000);
        employee.setJob(testJob);
        Employee saved = employeeRepository.save(employee);

        // When & Then
        mockMvc.perform(get("/web/employees/{id}/edit", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/form"))
                .andExpect(model().attributeExists("employeeDTO"))
                .andExpect(model().attributeExists("jobs"))
                .andExpect(model().attributeExists("employees"));
    }

    @Test
    void testShowEmployeeDetails() throws Exception {
        // Given
        Employee employee = new Employee();
        employee.setFirstName("Bob");
        employee.setLastName("Wilson");
        employee.setPhoneNumber("2222222222");
        employee.setEmail("bob@example.com");
        employee.setSalary(52000);
        employee.setJob(testJob);
        Employee saved = employeeRepository.save(employee);

        // When & Then
        mockMvc.perform(get("/web/employees/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/details"))
                .andExpect(model().attributeExists("employee"));
    }

    @Test
    void testCreateEmployeeSuccess() throws Exception {
        // When & Then
        mockMvc.perform(post("/web/employees")
                .param("firstName", "Charlie")
                .param("lastName", "Brown")
                .param("phoneNumber", "3333333333")
                .param("email", "charlie@example.com")
                .param("salary", "48000")
                .param("flightHours", "100")
                .param("jobId", testJob.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/employees"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify employee was created
        Employee fromDb = employeeRepository.findAll().get(0);
        assertThat(fromDb.getFirstName()).isEqualTo("Charlie");
        assertThat(fromDb.getLastName()).isEqualTo("Brown");
        assertThat(fromDb.getEmail()).isEqualTo("charlie@example.com");
        assertThat(fromDb.getSalary()).isEqualTo(48000);
    }

    @Test
    void testCreateEmployeeValidationError() throws Exception {
        // When & Then - Missing required fields
        mockMvc.perform(post("/web/employees")
                .param("firstName", "") // Empty first name
                .param("lastName", "") // Empty last name
                .param("email", "invalid-email") // Invalid email format
                .param("salary", "-1000") // Negative salary
                .param("jobId", testJob.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/form"))
                .andExpect(model().attributeExists("employeeDTO"))
                .andExpect(model().attributeExists("jobs"))
                .andExpect(model().attributeExists("employees"))
                .andExpect(model().hasErrors());

        // Verify no employee was created
        assertThat(employeeRepository.findAll()).isEmpty();
    }

    @Test
    void testUpdateEmployeeSuccess() throws Exception {
        // Given
        Employee existingEmployee = new Employee();
        existingEmployee.setFirstName("David");
        existingEmployee.setLastName("Miller");
        existingEmployee.setPhoneNumber("4444444444");
        existingEmployee.setEmail("david@example.com");
        existingEmployee.setSalary(51000);
        existingEmployee.setJob(testJob);
        Employee saved = employeeRepository.save(existingEmployee);

        // When & Then
        mockMvc.perform(post("/web/employees/{id}", saved.getId())
                .param("firstName", "David")
                .param("lastName", "Miller-Updated")
                .param("phoneNumber", "5555555555")
                .param("email", "david.updated@example.com")
                .param("salary", "60000")
                .param("flightHours", "200")
                .param("jobId", testJob.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/employees"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify employee was updated
        Employee fromDb = employeeRepository.findById(saved.getId()).orElse(null);
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getLastName()).isEqualTo("Miller-Updated");
        assertThat(fromDb.getEmail()).isEqualTo("david.updated@example.com");
        assertThat(fromDb.getSalary()).isEqualTo(60000);
    }

    @Test
    void testDeleteEmployeeSuccess() throws Exception {
        // Given
        Employee employee = new Employee();
        employee.setFirstName("Emma");
        employee.setLastName("Davis");
        employee.setPhoneNumber("6666666666");
        employee.setEmail("emma@example.com");
        employee.setSalary(47000);
        employee.setJob(testJob);
        Employee saved = employeeRepository.save(employee);

        // When & Then
        mockMvc.perform(post("/web/employees/{id}/delete", saved.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/employees"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify employee was deleted
        assertThat(employeeRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testShowEmployeeNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/web/employees/{id}", 9999))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/list"))
                .andExpect(model().attributeExists("employees"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void testEditEmployeeNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/web/employees/{id}/edit", 9999))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/employees"));
    }

    @Test
    void testDeleteEmployeeNotFound() throws Exception {
        // When & Then - Should still redirect with success (idempotent delete)
        mockMvc.perform(post("/web/employees/{id}/delete", 9999))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/employees"))
                .andExpect(flash().attributeExists("successMessage"));
    }
}
