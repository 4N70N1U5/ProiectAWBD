package com.antonio.skybase.repositories;

import com.antonio.skybase.config.TestConfig;
import com.antonio.skybase.entities.Department;
import com.antonio.skybase.entities.Employee;
import com.antonio.skybase.entities.Job;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@ActiveProfiles("test")
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Job testJob;

    @BeforeEach
    void setUp() {
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
    void testSaveAndFindById() {
        // Given
        Employee employee = new Employee();
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setPhoneNumber("1234567890");
        employee.setEmail("john.doe@example.com");
        employee.setSalary(50000);
        employee.setJob(testJob);

        // When
        Employee saved = employeeRepository.save(employee);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("John");
        assertThat(saved.getLastName()).isEqualTo("Doe");

        Optional<Employee> found = employeeRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void testFindAvailableEmployeesByDate() {
        // Given
        Employee emp1 = new Employee();
        emp1.setFirstName("Alice");
        emp1.setLastName("Johnson");
        emp1.setPhoneNumber("2222222222");
        emp1.setEmail("alice@example.com");
        emp1.setSalary(45000);
        emp1.setJob(testJob);
        Employee savedEmp1 = employeeRepository.save(emp1);

        Employee emp2 = new Employee();
        emp2.setFirstName("Charlie");
        emp2.setLastName("Brown");
        emp2.setPhoneNumber("3333333333");
        emp2.setEmail("charlie@example.com");
        emp2.setSalary(52000);
        emp2.setJob(testJob);
        Employee savedEmp2 = employeeRepository.save(emp2);

        // Note: Since we don't have EmployeeAssignment setup in this test,
        // all employees should be available for any date
        java.time.LocalDate testDate = java.time.LocalDate.of(2023, 10, 15);

        // When
        List<Employee> availableEmployees = employeeRepository.findAvailableEmployeesByDate(testDate);

        // Then
        assertThat(availableEmployees).hasSize(2);
        assertThat(availableEmployees).extracting(Employee::getFirstName)
                .containsExactlyInAnyOrder("Alice", "Charlie");
        // Verify that our saved employees are included
        assertThat(availableEmployees).contains(savedEmp1, savedEmp2);
    }

    @Test
    void testFindAll() {
        // Given
        Employee emp1 = new Employee();
        emp1.setFirstName("Alice");
        emp1.setLastName("Johnson");
        emp1.setPhoneNumber("2222222222");
        emp1.setEmail("alice@example.com");
        emp1.setSalary(45000);
        emp1.setJob(testJob);

        Employee emp2 = new Employee();
        emp2.setFirstName("Charlie");
        emp2.setLastName("Brown");
        emp2.setPhoneNumber("3333333333");
        emp2.setEmail("charlie@example.com");
        emp2.setSalary(52000);
        emp2.setJob(testJob);

        employeeRepository.save(emp1);
        employeeRepository.save(emp2);

        // When
        List<Employee> employees = employeeRepository.findAll();

        // Then
        assertThat(employees).hasSize(2);
        assertThat(employees).extracting(Employee::getFirstName)
                .containsExactlyInAnyOrder("Alice", "Charlie");
    }

    @Test
    void testDeleteById() {
        // Given
        Employee employee = new Employee();
        employee.setFirstName("David");
        employee.setLastName("Miller");
        employee.setPhoneNumber("4444444444");
        employee.setEmail("david@example.com");
        employee.setSalary(47000);
        employee.setJob(testJob);
        Employee saved = employeeRepository.save(employee);

        // When
        employeeRepository.deleteById(saved.getId());

        // Then
        Optional<Employee> found = employeeRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void testUpdate() {
        // Given
        Employee employee = new Employee();
        employee.setFirstName("Helen");
        employee.setLastName("Taylor");
        employee.setPhoneNumber("8888888888");
        employee.setEmail("helen@example.com");
        employee.setSalary(46000);
        employee.setJob(testJob);
        Employee saved = employeeRepository.save(employee);

        // When
        saved.setLastName("Taylor-Updated");
        saved.setSalary(50000);
        saved.setEmail("helen.updated@example.com");
        Employee updated = employeeRepository.save(saved);

        // Then
        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getLastName()).isEqualTo("Taylor-Updated");
        assertThat(updated.getSalary()).isEqualTo(50000);
        assertThat(updated.getEmail()).isEqualTo("helen.updated@example.com");

        // Verify from database
        Optional<Employee> fromDb = employeeRepository.findById(saved.getId());
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().getLastName()).isEqualTo("Taylor-Updated");
        assertThat(fromDb.get().getEmail()).isEqualTo("helen.updated@example.com");
    }
}
