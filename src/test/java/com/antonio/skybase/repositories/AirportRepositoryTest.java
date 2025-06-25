package com.antonio.skybase.repositories;

import com.antonio.skybase.config.TestConfig;
import com.antonio.skybase.entities.Airport;
import com.antonio.skybase.entities.City;
import com.antonio.skybase.entities.Country;
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
class AirportRepositoryTest {

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CountryRepository countryRepository;

    private Country testCountry;
    private City testCity;

    @BeforeEach
    void setUp() {
        airportRepository.deleteAll();
        cityRepository.deleteAll();
        countryRepository.deleteAll();

        // Create test data
        testCountry = new Country();
        testCountry.setName("Test Country");
        testCountry.setCode("TC");
        testCountry = countryRepository.save(testCountry);

        testCity = new City();
        testCity.setName("Test City");
        testCity.setCountry(testCountry);
        testCity = cityRepository.save(testCity);
    }

    @Test
    void testSaveAndFindAirport() {
        // Given
        Airport airport = createTestAirport("Test Airport", "TST");

        // When
        Airport savedAirport = airportRepository.save(airport);

        // Then
        assertThat(savedAirport.getId()).isNotNull();
        assertThat(savedAirport.getName()).isEqualTo("Test Airport");
        assertThat(savedAirport.getCode()).isEqualTo("TST");
        assertThat(savedAirport.getCity()).isEqualTo(testCity);

        // Verify we can find it
        Optional<Airport> found = airportRepository.findById(savedAirport.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Airport");
    }

    @Test
    void testExistsByCode() {
        // Given
        Airport airport = createTestAirport("Test Airport", "TST");
        airportRepository.save(airport);

        // When & Then
        assertThat(airportRepository.existsByCode("TST")).isTrue();
        assertThat(airportRepository.existsByCode("XXX")).isFalse();
    }

    @Test
    void testFindByCode() {
        // Given
        Airport airport = createTestAirport("Test Airport", "TST");
        airportRepository.save(airport);

        // When
        Airport found = airportRepository.findByCode("TST");

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Test Airport");
        assertThat(found.getCode()).isEqualTo("TST");
    }

    @Test
    void testFindByCodeNotFound() {
        // When
        Airport found = airportRepository.findByCode("XXX");

        // Then
        assertThat(found).isNull();
    }

    @Test
    void testFindAll() {
        // Given
        Airport airport1 = createTestAirport("Airport 1", "AP1");
        Airport airport2 = createTestAirport("Airport 2", "AP2");
        airportRepository.save(airport1);
        airportRepository.save(airport2);

        // When
        List<Airport> airports = airportRepository.findAll();

        // Then
        assertThat(airports).hasSize(2);
        assertThat(airports).extracting(Airport::getName)
                .containsExactlyInAnyOrder("Airport 1", "Airport 2");
    }

    @Test
    void testDeleteAirport() {
        // Given
        Airport airport = createTestAirport("Test Airport", "TST");
        Airport savedAirport = airportRepository.save(airport);

        // When
        airportRepository.deleteById(savedAirport.getId());

        // Then
        Optional<Airport> found = airportRepository.findById(savedAirport.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void testUpdateAirport() {
        // Given
        Airport airport = createTestAirport("Test Airport", "TST");
        Airport savedAirport = airportRepository.save(airport);

        // When
        savedAirport.setName("Updated Airport");
        savedAirport.setCode("UPD");
        Airport updatedAirport = airportRepository.save(savedAirport);

        // Then
        assertThat(updatedAirport.getName()).isEqualTo("Updated Airport");
        assertThat(updatedAirport.getCode()).isEqualTo("UPD");

        // Verify in database
        Optional<Airport> found = airportRepository.findById(savedAirport.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Updated Airport");
        assertThat(found.get().getCode()).isEqualTo("UPD");
    }

    private Airport createTestAirport(String name, String code) {
        Airport airport = new Airport();
        airport.setName(name);
        airport.setCode(code);
        airport.setCity(testCity);
        return airport;
    }
}
