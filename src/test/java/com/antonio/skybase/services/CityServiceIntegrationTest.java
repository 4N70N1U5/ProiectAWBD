package com.antonio.skybase.services;

import com.antonio.skybase.config.TestConfig;
import com.antonio.skybase.dtos.CityDTO;
import com.antonio.skybase.entities.City;
import com.antonio.skybase.entities.Country;
import com.antonio.skybase.exceptions.BadRequestException;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.repositories.CityRepository;
import com.antonio.skybase.repositories.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestConfig.class)
@ActiveProfiles("test")
@Transactional
class CityServiceIntegrationTest {

    @Autowired
    private CityService cityService;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CountryRepository countryRepository;

    private Country testCountry;

    @BeforeEach
    void setUp() {
        cityRepository.deleteAll();
        countryRepository.deleteAll();

        // Create test country
        testCountry = new Country();
        testCountry.setName("Test Country");
        testCountry.setCode("TC");
        testCountry = countryRepository.save(testCountry);
    }

    @Test
    void testCreateCitySuccess() {
        // Given
        CityDTO cityDTO = new CityDTO();
        cityDTO.setName("Integration Test City");
        cityDTO.setCountryId(testCountry.getId());

        // When
        City created = cityService.create(cityDTO);

        // Then
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Integration Test City");
        assertThat(created.getCountry()).isEqualTo(testCountry);

        // Verify it's actually in the database
        assertThat(cityRepository.existsById(created.getId())).isTrue();
    }

    @Test
    void testCreateCityWithInvalidCountry() {
        // Given
        CityDTO cityDTO = new CityDTO();
        cityDTO.setName("Test City");
        cityDTO.setCountryId(999);

        // When & Then
        assertThatThrownBy(() -> cityService.create(cityDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Country with ID 999 does not exist");
    }

    @Test
    void testGetAllCities() {
        // Given
        City city1 = new City();
        city1.setName("City 1");
        city1.setCountry(testCountry);

        City city2 = new City();
        city2.setName("City 2");
        city2.setCountry(testCountry);

        cityRepository.save(city1);
        cityRepository.save(city2);

        // When
        List<City> cities = cityService.getAll();

        // Then
        assertThat(cities).hasSize(2);
        assertThat(cities).extracting(City::getName)
                .containsExactlyInAnyOrder("City 1", "City 2");
    }

    @Test
    void testGetCityByIdSuccess() {
        // Given
        City savedCity = cityRepository.save(createTestCity());

        // When
        City found = cityService.getById(savedCity.getId());

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(savedCity.getId());
        assertThat(found.getName()).isEqualTo("Test City");
    }

    @Test
    void testGetCityByIdNotFound() {
        // When & Then
        assertThatThrownBy(() -> cityService.getById(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("City with ID 999 not found");
    }

    @Test
    void testUpdateCitySuccess() {
        // Given
        City savedCity = cityRepository.save(createTestCity());

        CityDTO cityDTO = new CityDTO();
        cityDTO.setName("Updated City");
        cityDTO.setCountryId(testCountry.getId());

        // When
        City updated = cityService.update(savedCity.getId(), cityDTO);

        // Then
        assertThat(updated.getName()).isEqualTo("Updated City");
        assertThat(updated.getCountry()).isEqualTo(testCountry);

        // Verify in database
        City fromDb = cityRepository.findById(savedCity.getId()).orElse(null);
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getName()).isEqualTo("Updated City");
    }

    @Test
    void testUpdateCityWithInvalidCountry() {
        // Given
        City savedCity = cityRepository.save(createTestCity());

        CityDTO cityDTO = new CityDTO();
        cityDTO.setName("Updated City");
        cityDTO.setCountryId(999);

        // When & Then
        assertThatThrownBy(() -> cityService.update(savedCity.getId(), cityDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Country with ID 999 does not exist");
    }

    @Test
    void testUpdateCityNotFound() {
        // Given
        CityDTO cityDTO = new CityDTO();
        cityDTO.setName("Updated City");
        cityDTO.setCountryId(testCountry.getId());

        // When & Then
        assertThatThrownBy(() -> cityService.update(999, cityDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("City with ID 999 not found");
    }

    @Test
    void testDeleteCitySuccess() {
        // Given
        City savedCity = cityRepository.save(createTestCity());

        // When
        cityService.delete(savedCity.getId());

        // Then
        assertThat(cityRepository.existsById(savedCity.getId())).isFalse();
    }

    private City createTestCity() {
        City city = new City();
        city.setName("Test City");
        city.setCountry(testCountry);
        return city;
    }
}
