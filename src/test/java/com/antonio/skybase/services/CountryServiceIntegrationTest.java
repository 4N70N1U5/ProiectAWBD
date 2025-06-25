package com.antonio.skybase.services;

import com.antonio.skybase.config.TestConfig;
import com.antonio.skybase.entities.Country;
import com.antonio.skybase.exceptions.BadRequestException;
import com.antonio.skybase.exceptions.NotFoundException;
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
class CountryServiceIntegrationTest {

    @Autowired
    private CountryService countryService;

    @Autowired
    private CountryRepository countryRepository;

    @BeforeEach
    void setUp() {
        countryRepository.deleteAll();
    }

    @Test
    void testCreateCountrySuccess() {
        // Given
        Country country = new Country();
        country.setName("Integration Test Country");
        country.setCode("IT");

        // When
        Country created = countryService.create(country);

        // Then
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Integration Test Country");
        assertThat(created.getCode()).isEqualTo("IT");

        // Verify it's actually in the database
        assertThat(countryRepository.existsByCode("IT")).isTrue();
    }

    @Test
    void testCreateCountryWithDuplicateCode() {
        // Given
        Country existingCountry = new Country();
        existingCountry.setName("Existing Country");
        existingCountry.setCode("EX");
        countryRepository.save(existingCountry);

        Country duplicateCountry = new Country();
        duplicateCountry.setName("Duplicate Country");
        duplicateCountry.setCode("EX"); // Same code

        // When & Then
        assertThatThrownBy(() -> countryService.create(duplicateCountry))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void testGetAllCountries() {
        // Given
        Country country1 = new Country();
        country1.setName("Country 1");
        country1.setCode("C1");

        Country country2 = new Country();
        country2.setName("Country 2");
        country2.setCode("C2");

        countryRepository.save(country1);
        countryRepository.save(country2);

        // When
        List<Country> countries = countryService.getAll();

        // Then
        assertThat(countries).hasSize(2);
        assertThat(countries).extracting(Country::getName)
                .containsExactlyInAnyOrder("Country 1", "Country 2");
    }

    @Test
    void testGetCountryByIdSuccess() {
        // Given
        Country savedCountry = countryRepository.save(createTestCountry());

        // When
        Country found = countryService.getById(savedCountry.getId());

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(savedCountry.getId());
        assertThat(found.getName()).isEqualTo("Test Country");
    }

    @Test
    void testGetCountryByIdNotFound() {
        // When & Then
        assertThatThrownBy(() -> countryService.getById(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Country with ID 999 not found");
    }

    @Test
    void testUpdateCountrySuccess() {
        // Given
        Country savedCountry = countryRepository.save(createTestCountry());

        savedCountry.setName("Updated Country");
        savedCountry.setCode("UC");

        // When
        Country updated = countryService.update(savedCountry.getId(), savedCountry);

        // Then
        assertThat(updated.getName()).isEqualTo("Updated Country");
        assertThat(updated.getCode()).isEqualTo("UC");

        // Verify in database
        Country fromDb = countryRepository.findById(savedCountry.getId()).orElse(null);
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getName()).isEqualTo("Updated Country");
    }

    @Test
    void testUpdateCountryWithDuplicateCode() {
        // Given
        Country country1 = new Country();
        country1.setName("Country 1");
        country1.setCode("C1");
        countryRepository.save(country1);

        Country country2 = new Country();
        country2.setName("Country 2");
        country2.setCode("C2");
        Country saved2 = countryRepository.save(country2);

        // Create a new country object with the duplicate code for update
        Country updateData = new Country();
        updateData.setName("Updated Country 2");
        updateData.setCode("C1"); // Trying to use existing code

        // When & Then
        assertThatThrownBy(() -> countryService.update(saved2.getId(), updateData))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void testDeleteCountrySuccess() {
        // Given
        Country savedCountry = countryRepository.save(createTestCountry());

        // When
        countryService.delete(savedCountry.getId());

        // Then
        assertThat(countryRepository.existsById(savedCountry.getId())).isFalse();
    }

    private Country createTestCountry() {
        Country country = new Country();
        country.setName("Test Country");
        country.setCode("TC");
        return country;
    }
}
