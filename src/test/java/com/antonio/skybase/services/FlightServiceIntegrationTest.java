package com.antonio.skybase.services;

import com.antonio.skybase.config.TestConfig;
import com.antonio.skybase.dtos.FlightDTO;
import com.antonio.skybase.entities.Airport;
import com.antonio.skybase.entities.City;
import com.antonio.skybase.entities.Country;
import com.antonio.skybase.entities.Flight;
import com.antonio.skybase.exceptions.BadRequestException;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.repositories.AirportRepository;
import com.antonio.skybase.repositories.CityRepository;
import com.antonio.skybase.repositories.CountryRepository;
import com.antonio.skybase.repositories.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Import(TestConfig.class)
@ActiveProfiles("test")
@Transactional
class FlightServiceIntegrationTest {

    @Autowired
    private FlightService flightService;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CountryRepository countryRepository;

    private Airport departureAirport;
    private Airport arrivalAirport;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        flightRepository.deleteAll();
        airportRepository.deleteAll();
        cityRepository.deleteAll();
        countryRepository.deleteAll();

        // Setup test data
        Country country = new Country();
        country.setName("Test Country");
        country.setCode("TC");
        country = countryRepository.save(country);

        City city1 = new City();
        city1.setName("Test City 1");
        city1.setCountry(country);
        city1 = cityRepository.save(city1);

        City city2 = new City();
        city2.setName("Test City 2");
        city2.setCountry(country);
        city2 = cityRepository.save(city2);

        departureAirport = new Airport();
        departureAirport.setName("Test Departure Airport");
        departureAirport.setCode("TDA");
        departureAirport.setCity(city1);
        departureAirport = airportRepository.save(departureAirport);

        arrivalAirport = new Airport();
        arrivalAirport.setName("Test Arrival Airport");
        arrivalAirport.setCode("TAA");
        arrivalAirport.setCity(city2);
        arrivalAirport = airportRepository.save(arrivalAirport);
    }

    @Test
    void testCreateFlightSuccess() {
        // Given
        FlightDTO flightDTO = new FlightDTO();
        flightDTO.setNumber("FL123");
        flightDTO.setDepartureTime(LocalTime.of(10, 30));
        flightDTO.setArrivalTime(LocalTime.of(14, 45));
        flightDTO.setDistance(1200);
        flightDTO.setDepartureAirportId(departureAirport.getId());
        flightDTO.setArrivalAirportId(arrivalAirport.getId());

        // When
        Flight createdFlight = flightService.create(flightDTO);

        // Then
        assertThat(createdFlight).isNotNull();
        assertThat(createdFlight.getId()).isNotNull();
        assertThat(createdFlight.getNumber()).isEqualTo("FL123");
        assertThat(createdFlight.getDepartureTime()).isEqualTo(LocalTime.of(10, 30));
        assertThat(createdFlight.getArrivalTime()).isEqualTo(LocalTime.of(14, 45));
        assertThat(createdFlight.getDistance()).isEqualTo(1200);
        assertThat(createdFlight.getDepartureAirport().getId()).isEqualTo(departureAirport.getId());
        assertThat(createdFlight.getArrivalAirport().getId()).isEqualTo(arrivalAirport.getId());

        // Verify it's saved in database
        Flight fromDb = flightRepository.findById(createdFlight.getId()).orElse(null);
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getNumber()).isEqualTo("FL123");
    }

    @Test
    void testCreateFlightWithInvalidDepartureAirport() {
        // Given
        FlightDTO flightDTO = new FlightDTO();
        flightDTO.setNumber("FL456");
        flightDTO.setDepartureTime(LocalTime.of(10, 30));
        flightDTO.setArrivalTime(LocalTime.of(14, 45));
        flightDTO.setDistance(1200);
        flightDTO.setDepartureAirportId(9999); // Non-existent airport
        flightDTO.setArrivalAirportId(arrivalAirport.getId());

        // When & Then
        assertThrows(BadRequestException.class, () -> flightService.create(flightDTO));

        // Verify nothing was saved
        List<Flight> flights = flightRepository.findAll();
        assertThat(flights).isEmpty();
    }

    @Test
    void testCreateFlightWithInvalidArrivalAirport() {
        // Given
        FlightDTO flightDTO = new FlightDTO();
        flightDTO.setNumber("FL789");
        flightDTO.setDepartureTime(LocalTime.of(10, 30));
        flightDTO.setArrivalTime(LocalTime.of(14, 45));
        flightDTO.setDistance(1200);
        flightDTO.setDepartureAirportId(departureAirport.getId());
        flightDTO.setArrivalAirportId(9999); // Non-existent airport

        // When & Then
        assertThrows(BadRequestException.class, () -> flightService.create(flightDTO));

        // Verify nothing was saved
        List<Flight> flights = flightRepository.findAll();
        assertThat(flights).isEmpty();
    }

    @Test
    void testGetAllFlights() {
        // Given
        Flight flight1 = new Flight();
        flight1.setNumber("FL001");
        flight1.setDepartureTime(LocalTime.of(8, 0));
        flight1.setArrivalTime(LocalTime.of(12, 0));
        flight1.setDistance(800);
        flight1.setDepartureAirport(departureAirport);
        flight1.setArrivalAirport(arrivalAirport);
        flightRepository.save(flight1);

        Flight flight2 = new Flight();
        flight2.setNumber("FL002");
        flight2.setDepartureTime(LocalTime.of(16, 0));
        flight2.setArrivalTime(LocalTime.of(20, 0));
        flight2.setDistance(1000);
        flight2.setDepartureAirport(arrivalAirport);
        flight2.setArrivalAirport(departureAirport);
        flightRepository.save(flight2);

        // When
        List<Flight> flights = flightService.getAll();

        // Then
        assertThat(flights).hasSize(2);
        assertThat(flights).extracting(Flight::getNumber)
                .containsExactlyInAnyOrder("FL001", "FL002");
    }

    @Test
    void testGetFlightByIdSuccess() {
        // Given
        Flight flight = new Flight();
        flight.setNumber("FL999");
        flight.setDepartureTime(LocalTime.of(14, 30));
        flight.setArrivalTime(LocalTime.of(18, 30));
        flight.setDistance(900);
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        Flight saved = flightRepository.save(flight);

        // When
        Flight found = flightService.getById(saved.getId());

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getNumber()).isEqualTo("FL999");
        assertThat(found.getDepartureTime()).isEqualTo(LocalTime.of(14, 30));
    }

    @Test
    void testGetFlightByIdNotFound() {
        // When & Then
        assertThrows(NotFoundException.class, () -> flightService.getById(9999));
    }

    @Test
    void testUpdateFlightSuccess() {
        // Given
        Flight existingFlight = new Flight();
        existingFlight.setNumber("FL888");
        existingFlight.setDepartureTime(LocalTime.of(9, 0));
        existingFlight.setArrivalTime(LocalTime.of(13, 0));
        existingFlight.setDistance(700);
        existingFlight.setDepartureAirport(departureAirport);
        existingFlight.setArrivalAirport(arrivalAirport);
        Flight saved = flightRepository.save(existingFlight);

        FlightDTO updateDTO = new FlightDTO();
        updateDTO.setNumber("FL888-UPDATED");
        updateDTO.setDepartureTime(LocalTime.of(10, 0));
        updateDTO.setArrivalTime(LocalTime.of(14, 0));
        updateDTO.setDistance(750);
        updateDTO.setDepartureAirportId(departureAirport.getId());
        updateDTO.setArrivalAirportId(arrivalAirport.getId());

        // When
        Flight updated = flightService.update(saved.getId(), updateDTO);

        // Then
        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getNumber()).isEqualTo("FL888-UPDATED");
        assertThat(updated.getDepartureTime()).isEqualTo(LocalTime.of(10, 0));
        assertThat(updated.getDistance()).isEqualTo(750);

        // Verify in database
        Flight fromDb = flightRepository.findById(saved.getId()).orElse(null);
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getNumber()).isEqualTo("FL888-UPDATED");
    }

    @Test
    void testDeleteFlightSuccess() {
        // Given
        Flight flight = new Flight();
        flight.setNumber("FL777");
        flight.setDepartureTime(LocalTime.of(11, 0));
        flight.setArrivalTime(LocalTime.of(15, 0));
        flight.setDistance(600);
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        Flight saved = flightRepository.save(flight);

        // When
        flightService.delete(saved.getId());

        // Then
        assertThat(flightRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testDeleteFlightNotFound() {
        // When & Then - Should not throw exception for non-existent flight
        // This is expected behavior - delete operations are idempotent
        flightService.delete(9999);

        // Verify no flights exist
        assertThat(flightRepository.findAll()).isEmpty();
    }
}
