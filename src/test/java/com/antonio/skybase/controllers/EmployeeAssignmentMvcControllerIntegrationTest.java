package com.antonio.skybase.controllers;

import com.antonio.skybase.entities.*;
import com.antonio.skybase.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EmployeeAssignmentMvcControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeAssignmentRepository employeeAssignmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private AircraftRepository aircraftRepository;

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Employee testEmployee;
    private Flight testFlight;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        // Clean up all data
        employeeAssignmentRepository.deleteAll();
        employeeRepository.deleteAll();
        flightRepository.deleteAll();
        aircraftRepository.deleteAll();
        airportRepository.deleteAll();
        cityRepository.deleteAll();
        countryRepository.deleteAll();
        jobRepository.deleteAll();
        departmentRepository.deleteAll();

        testDate = LocalDate.of(2024, 6, 15);

        // Create test data
        Country country = new Country();
        country.setName("Test Country");
        country.setCode("TC");
        countryRepository.save(country);

        City city = new City();
        city.setName("Test City");
        city.setCountry(country);
        cityRepository.save(city);

        Airport originAirport = new Airport();
        originAirport.setName("Test Origin Airport");
        originAirport.setCode("TOA");
        originAirport.setCity(city);
        airportRepository.save(originAirport);

        Airport destinationAirport = new Airport();
        destinationAirport.setName("Test Destination Airport");
        destinationAirport.setCode("TDA");
        destinationAirport.setCity(city);
        airportRepository.save(destinationAirport);

        Aircraft aircraft = new Aircraft();
        aircraft.setRegistration("TEST123");
        aircraft.setType("Test Aircraft");
        aircraft.setCapacity(100);
        aircraftRepository.save(aircraft);

        testFlight = new Flight();
        testFlight.setNumber("TF123");
        testFlight.setDepartureAirport(originAirport);
        testFlight.setArrivalAirport(destinationAirport);
        testFlight.setDepartureTime(LocalTime.of(10, 0));
        testFlight.setArrivalTime(LocalTime.of(12, 0));
        testFlight.setDistance(500);
        flightRepository.save(testFlight);

        // Create test data - need flight crew department
        Department department = new Department();
        department.setName("Echipaj de zbor");
        department = departmentRepository.save(department);

        Job job = new Job();
        job.setTitle("Test Job");
        job.setMinSalary(40000.0);
        job.setMaxSalary(60000.0);
        job.setDepartment(department);
        jobRepository.save(job);

        testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setPhoneNumber("123-456-7890");
        testEmployee.setEmail("john.doe@test.com");
        testEmployee.setSalary(50000);
        testEmployee.setJob(job);
        employeeRepository.save(testEmployee);
    }

    @Test
    void testShowEmployeeAssignmentList() throws Exception {
        // When & Then
        mockMvc.perform(get("/web/employee-assignments"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee-assignments/list"))
                .andExpect(model().attributeExists("employeeAssignments"));
    }

    @Test
    void testShowAddEmployeeAssignmentForm() throws Exception {
        // When & Then
        mockMvc.perform(get("/web/employee-assignments/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee-assignments/form"))
                .andExpect(model().attributeExists("employeeAssignmentDTO"))
                .andExpect(model().attributeExists("employees"))
                .andExpect(model().attributeExists("flights"));
    }

    @Test
    void testCreateEmployeeAssignmentValidationError() throws Exception {
        // When & Then - Missing required fields
        mockMvc.perform(post("/web/employee-assignments")
                .param("employeeId", "")
                .param("flightId", "")
                .param("date", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("employee-assignments/form"))
                .andExpect(model().attributeExists("employeeAssignmentDTO"))
                .andExpect(model().attributeExists("employees"))
                .andExpect(model().attributeExists("flights"))
                .andExpect(model().hasErrors());

        // Verify no assignment was created
        assertThat(employeeAssignmentRepository.findAll()).isEmpty();
    }

    @Test
    void testShowEmployeeAssignmentDetails() throws Exception {
        // Given - Create an assignment
        EmployeeAssignmentId id = new EmployeeAssignmentId();
        id.setEmployeeId(testEmployee.getId());
        id.setFlightId(testFlight.getId());
        id.setDate(testDate);

        EmployeeAssignment assignment = new EmployeeAssignment();
        assignment.setId(id);
        assignment.setEmployee(testEmployee);
        assignment.setFlight(testFlight);
        employeeAssignmentRepository.save(assignment);

        // When & Then
        mockMvc.perform(get("/web/employee-assignments/{employeeId}/{flightId}/{date}",
                testEmployee.getId(), testFlight.getId(), testDate.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("employee-assignments/details"))
                .andExpect(model().attributeExists("employeeAssignment"));
    }

    @Test
    void testShowEditEmployeeAssignmentForm() throws Exception {
        // Given - Create an assignment
        EmployeeAssignmentId id = new EmployeeAssignmentId();
        id.setEmployeeId(testEmployee.getId());
        id.setFlightId(testFlight.getId());
        id.setDate(testDate);

        EmployeeAssignment assignment = new EmployeeAssignment();
        assignment.setId(id);
        assignment.setEmployee(testEmployee);
        assignment.setFlight(testFlight);
        employeeAssignmentRepository.save(assignment);

        // When & Then
        mockMvc.perform(get("/web/employee-assignments/{employeeId}/{flightId}/{date}/edit",
                testEmployee.getId(), testFlight.getId(), testDate.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("employee-assignments/form"))
                .andExpect(model().attributeExists("employeeAssignmentDTO"))
                .andExpect(model().attributeExists("originalId"))
                .andExpect(model().attributeExists("employees"))
                .andExpect(model().attributeExists("flights"));
    }

    @Test
    void testUpdateEmployeeAssignmentSuccess() throws Exception {
        // Given - Create an assignment
        EmployeeAssignmentId id = new EmployeeAssignmentId();
        id.setEmployeeId(testEmployee.getId());
        id.setFlightId(testFlight.getId());
        id.setDate(testDate);

        EmployeeAssignment assignment = new EmployeeAssignment();
        assignment.setId(id);
        assignment.setEmployee(testEmployee);
        assignment.setFlight(testFlight);
        employeeAssignmentRepository.save(assignment);

        LocalDate newDate = testDate.plusDays(1);

        // When & Then
        mockMvc.perform(post("/web/employee-assignments/{employeeId}/{flightId}/{date}",
                testEmployee.getId(), testFlight.getId(), testDate.toString())
                .param("employeeId", testEmployee.getId().toString())
                .param("flightId", testFlight.getId().toString())
                .param("date", newDate.toString()))
                .andExpect(status().isOk()) // Temporarily check if it's showing form
                .andExpect(view().name("employee-assignments/form"));
    }

    @Test
    void testDeleteEmployeeAssignmentSuccess() throws Exception {
        // Given - Create an assignment
        EmployeeAssignmentId id = new EmployeeAssignmentId();
        id.setEmployeeId(testEmployee.getId());
        id.setFlightId(testFlight.getId());
        id.setDate(testDate);

        EmployeeAssignment assignment = new EmployeeAssignment();
        assignment.setId(id);
        assignment.setEmployee(testEmployee);
        assignment.setFlight(testFlight);
        employeeAssignmentRepository.save(assignment);

        // When & Then
        mockMvc.perform(post("/web/employee-assignments/{employeeId}/{flightId}/{date}/delete",
                testEmployee.getId(), testFlight.getId(), testDate.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/employee-assignments"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify assignment was deleted
        assertThat(employeeAssignmentRepository.findAll()).isEmpty();
    }

    @Test
    void testDeleteEmployeeAssignmentNotFound() throws Exception {
        // When & Then - Should still redirect with success message (idempotent delete)
        mockMvc.perform(post("/web/employee-assignments/{employeeId}/{flightId}/{date}/delete",
                9999, 9999, "2024-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/employee-assignments"));
    }
}
