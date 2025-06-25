package com.antonio.skybase.services;

import com.antonio.skybase.config.TestConfig;
import com.antonio.skybase.dtos.EmployeeDTO;
import com.antonio.skybase.entities.Department;
import com.antonio.skybase.entities.Employee;
import com.antonio.skybase.entities.Job;
import com.antonio.skybase.exceptions.BadRequestException;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.repositories.DepartmentRepository;
import com.antonio.skybase.repositories.EmployeeRepository;
import com.antonio.skybase.repositories.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Import(TestConfig.class)
@ActiveProfiles("test")
@Transactional
class EmployeeServiceIntegrationTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Job testJob;

    @BeforeEach
    void setUp() {
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
    void testCreateEmployeeSuccess() {
        // Given
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setFirstName("John");
        employeeDTO.setLastName("Doe");
        employeeDTO.setPhoneNumber("1234567890");
        employeeDTO.setEmail("john.doe@example.com");
        employeeDTO.setSalary(50000);
        employeeDTO.setJobId(testJob.getId());

        // When
        Employee createdEmployee = employeeService.create(employeeDTO);

        // Then
        assertThat(createdEmployee).isNotNull();
        assertThat(createdEmployee.getId()).isNotNull();
        assertThat(createdEmployee.getFirstName()).isEqualTo("John");
        assertThat(createdEmployee.getLastName()).isEqualTo("Doe");
        assertThat(createdEmployee.getPhoneNumber()).isEqualTo("1234567890");
        assertThat(createdEmployee.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(createdEmployee.getSalary()).isEqualTo(50000);
        assertThat(createdEmployee.getJob().getId()).isEqualTo(testJob.getId());

        // Verify it's saved in database
        Employee fromDb = employeeRepository.findById(createdEmployee.getId()).orElse(null);
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getFirstName()).isEqualTo("John");
        assertThat(fromDb.getLastName()).isEqualTo("Doe");
    }

    @Test
    void testCreateEmployeeWithInvalidJob() {
        // Given
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setFirstName("Jane");
        employeeDTO.setLastName("Smith");
        employeeDTO.setPhoneNumber("0987654321");
        employeeDTO.setEmail("jane.smith@example.com");
        employeeDTO.setSalary(55000);
        employeeDTO.setJobId(9999); // Non-existent job

        // When & Then
        assertThrows(BadRequestException.class, () -> employeeService.create(employeeDTO));

        // Verify nothing was saved
        List<Employee> employees = employeeRepository.findAll();
        assertThat(employees).isEmpty();
    }

    @Test
    void testGetAllEmployees() {
        // Given
        Employee emp1 = new Employee();
        emp1.setFirstName("Alice");
        emp1.setLastName("Johnson");
        emp1.setPhoneNumber("1111111111");
        emp1.setEmail("alice@example.com");
        emp1.setSalary(45000);
        emp1.setJob(testJob);
        employeeRepository.save(emp1);

        Employee emp2 = new Employee();
        emp2.setFirstName("Bob");
        emp2.setLastName("Wilson");
        emp2.setPhoneNumber("2222222222");
        emp2.setEmail("bob@example.com");
        emp2.setSalary(52000);
        emp2.setJob(testJob);
        employeeRepository.save(emp2);

        // When
        List<Employee> employees = employeeService.getAll();

        // Then
        assertThat(employees).hasSize(2);
        assertThat(employees).extracting(Employee::getFirstName)
                .containsExactlyInAnyOrder("Alice", "Bob");
    }

    @Test
    void testGetEmployeeByIdSuccess() {
        // Given
        Employee employee = new Employee();
        employee.setFirstName("Charlie");
        employee.setLastName("Brown");
        employee.setPhoneNumber("3333333333");
        employee.setEmail("charlie@example.com");
        employee.setSalary(48000);
        employee.setJob(testJob);
        Employee saved = employeeRepository.save(employee);

        // When
        Employee found = employeeService.getById(saved.getId());

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getFirstName()).isEqualTo("Charlie");
        assertThat(found.getLastName()).isEqualTo("Brown");
        assertThat(found.getEmail()).isEqualTo("charlie@example.com");
    }

    @Test
    void testGetEmployeeByIdNotFound() {
        // When & Then
        assertThrows(NotFoundException.class, () -> employeeService.getById(9999));
    }

    @Test
    void testUpdateEmployeeSuccess() {
        // Given
        Employee existingEmployee = new Employee();
        existingEmployee.setFirstName("David");
        existingEmployee.setLastName("Miller");
        existingEmployee.setPhoneNumber("4444444444");
        existingEmployee.setEmail("david@example.com");
        existingEmployee.setSalary(51000);
        existingEmployee.setJob(testJob);
        Employee saved = employeeRepository.save(existingEmployee);

        EmployeeDTO updateDTO = new EmployeeDTO();
        updateDTO.setFirstName("David");
        updateDTO.setLastName("Miller-Updated");
        updateDTO.setPhoneNumber("5555555555");
        updateDTO.setEmail("david.updated@example.com");
        updateDTO.setSalary(60000);
        updateDTO.setJobId(testJob.getId());

        // When
        Employee updated = employeeService.update(saved.getId(), updateDTO);

        // Then
        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getFirstName()).isEqualTo("David");
        assertThat(updated.getLastName()).isEqualTo("Miller-Updated");
        assertThat(updated.getPhoneNumber()).isEqualTo("5555555555");
        assertThat(updated.getEmail()).isEqualTo("david.updated@example.com");
        assertThat(updated.getSalary()).isEqualTo(60000);

        // Verify in database
        Employee fromDb = employeeRepository.findById(saved.getId()).orElse(null);
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getLastName()).isEqualTo("Miller-Updated");
        assertThat(fromDb.getEmail()).isEqualTo("david.updated@example.com");
    }

    @Test
    void testDeleteEmployeeSuccess() {
        // Given
        Employee employee = new Employee();
        employee.setFirstName("Grace");
        employee.setLastName("Wilson");
        employee.setPhoneNumber("8888888888");
        employee.setEmail("grace@example.com");
        employee.setSalary(49000);
        employee.setJob(testJob);
        Employee saved = employeeRepository.save(employee);

        // When
        employeeService.delete(saved.getId());

        // Then
        assertThat(employeeRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testDeleteEmployeeNotFound() {
        // When & Then - Should not throw exception for non-existent employee
        // This is expected behavior - delete operations are idempotent
        employeeService.delete(9999);

        // Verify no employees exist
        assertThat(employeeRepository.findAll()).isEmpty();
    }
}
