package com.antonio.skybase.testdata;

import com.antonio.skybase.entities.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Factory class for creating test data objects
 */
@Component
public class TestDataFactory {

    public Country createCountry(String name, String code) {
        Country country = new Country();
        country.setName(name);
        country.setCode(code);
        return country;
    }

    public City createCity(String name, Country country) {
        City city = new City();
        city.setName(name);
        city.setCountry(country);
        return city;
    }

    public Airport createAirport(String name, String code, City city) {
        Airport airport = new Airport();
        airport.setName(name);
        airport.setCode(code);
        airport.setCity(city);
        return airport;
    }

    public Department createDepartment(String name) {
        Department department = new Department();
        department.setName(name);
        return department;
    }

    public Job createJob(String title, Double minSalary, Double maxSalary, Department department) {
        Job job = new Job();
        job.setTitle(title);
        job.setMinSalary(minSalary);
        job.setMaxSalary(maxSalary);
        job.setDepartment(department);
        return job;
    }

    public Employee createEmployee(String firstName, String lastName, String email, String phone, Integer salary,
            Job job) {
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setEmail(email);
        employee.setPhoneNumber(phone);
        employee.setSalary(salary);
        employee.setJob(job);
        return employee;
    }

    public Aircraft createAircraft(String registration, String type, Integer range, Integer capacity) {
        Aircraft aircraft = new Aircraft();
        aircraft.setRegistration(registration);
        aircraft.setType(type);
        aircraft.setRange(range);
        aircraft.setCapacity(capacity);
        return aircraft;
    }

    public Flight createFlight(String number, Airport departureAirport, Airport arrivalAirport,
            LocalTime departureTime, LocalTime arrivalTime, Integer distance) {
        Flight flight = new Flight();
        flight.setNumber(number);
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        flight.setDepartureTime(departureTime);
        flight.setArrivalTime(arrivalTime);
        flight.setDistance(distance);
        return flight;
    }

    public AircraftAssignment createAircraftAssignment(Aircraft aircraft, Flight flight, LocalDate date) {
        AircraftAssignmentId id = new AircraftAssignmentId();
        id.setAircraftId(aircraft.getId());
        id.setFlightId(flight.getId());
        id.setDate(date);

        AircraftAssignment assignment = new AircraftAssignment();
        assignment.setId(id);
        assignment.setAircraft(aircraft);
        assignment.setFlight(flight);
        return assignment;
    }

    public EmployeeAssignment createEmployeeAssignment(Employee employee, Flight flight, LocalDate date) {
        EmployeeAssignmentId id = new EmployeeAssignmentId();
        id.setEmployeeId(employee.getId());
        id.setFlightId(flight.getId());
        id.setDate(date);

        EmployeeAssignment assignment = new EmployeeAssignment();
        assignment.setId(id);
        assignment.setEmployee(employee);
        assignment.setFlight(flight);
        return assignment;
    }

    // Default test data methods
    public Country createDefaultCountry() {
        return createCountry("Test Country", "TC");
    }

    public City createDefaultCity(Country country) {
        return createCity("Test City", country);
    }

    public Airport createDefaultAirport(City city) {
        return createAirport("Test Airport", "TST", city);
    }

    public Department createDefaultDepartment() {
        return createDepartment("Test Department");
    }

    public Job createDefaultJob(Department department) {
        return createJob("Test Job", 50000.0, 100000.0, department);
    }

    public Employee createDefaultEmployee(Job job) {
        return createEmployee("John", "Doe", "john.doe@test.com", "1234567890", 75000, job);
    }

    public Aircraft createDefaultAircraft() {
        return createAircraft("TEST001", "Test Aircraft", 5000, 200);
    }

    public Flight createDefaultFlight(Airport departureAirport, Airport arrivalAirport) {
        return createFlight("TEST123", departureAirport, arrivalAirport,
                LocalTime.of(10, 0), LocalTime.of(12, 0), 500);
    }
}
