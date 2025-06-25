package com.antonio.skybase.repositories;

import com.antonio.skybase.config.TestConfig;
import com.antonio.skybase.entities.Country;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@ActiveProfiles("test")
class CountryRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CountryRepository countryRepository;

    private Country testCountry;

    @BeforeEach
    void setUp() {
        testCountry = new Country();
        testCountry.setName("Test Country");
        testCountry.setCode("TC");
    }

    @Test
    void testSaveCountry() {
        // When
        Country savedCountry = countryRepository.save(testCountry);

        // Then
        assertThat(savedCountry).isNotNull();
        assertThat(savedCountry.getId()).isNotNull();
        assertThat(savedCountry.getName()).isEqualTo("Test Country");
        assertThat(savedCountry.getCode()).isEqualTo("TC");
    }

    @Test
    void testFindById() {
        // Given
        Country persistedCountry = entityManager.persistAndFlush(testCountry);

        // When
        Optional<Country> foundCountry = countryRepository.findById(persistedCountry.getId());

        // Then
        assertThat(foundCountry).isPresent();
        assertThat(foundCountry.get().getName()).isEqualTo("Test Country");
        assertThat(foundCountry.get().getCode()).isEqualTo("TC");
    }

    @Test
    void testFindAll() {
        // Given
        Country country1 = new Country();
        country1.setName("Country 1");
        country1.setCode("C1");

        Country country2 = new Country();
        country2.setName("Country 2");
        country2.setCode("C2");

        entityManager.persistAndFlush(country1);
        entityManager.persistAndFlush(country2);

        // When
        List<Country> countries = countryRepository.findAll();

        // Then
        assertThat(countries).hasSize(2);
        assertThat(countries).extracting(Country::getName)
                .containsExactlyInAnyOrder("Country 1", "Country 2");
    }

    @Test
    void testExistsByCode() {
        // Given
        entityManager.persistAndFlush(testCountry);

        // When & Then
        assertThat(countryRepository.existsByCode("TC")).isTrue();
        assertThat(countryRepository.existsByCode("XX")).isFalse();
    }

    @Test
    void testDeleteCountry() {
        // Given
        Country persistedCountry = entityManager.persistAndFlush(testCountry);

        // When
        countryRepository.deleteById(persistedCountry.getId());

        // Then
        Optional<Country> deletedCountry = countryRepository.findById(persistedCountry.getId());
        assertThat(deletedCountry).isEmpty();
    }

    @Test
    void testFindByCode() {
        // Given
        entityManager.persistAndFlush(testCountry);

        // When
        Country foundCountry = countryRepository.findByCode("TC");

        // Then
        assertThat(foundCountry).isNotNull();
        assertThat(foundCountry.getName()).isEqualTo("Test Country");
    }
}
