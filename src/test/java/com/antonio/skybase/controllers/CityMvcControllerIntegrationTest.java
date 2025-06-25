package com.antonio.skybase.controllers;

import com.antonio.skybase.config.TestConfig;
import com.antonio.skybase.entities.City;
import com.antonio.skybase.entities.Country;
import com.antonio.skybase.repositories.CityRepository;
import com.antonio.skybase.repositories.CountryRepository;
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
@ActiveProfiles("test")
@Transactional
@Import(TestConfig.class)
public class CityMvcControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CountryRepository countryRepository;

    private MockMvc mockMvc;

    private Country testCountry;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Clean up data before each test
        cityRepository.deleteAll();
        countryRepository.deleteAll();

        // Set up required data
        testCountry = new Country();
        testCountry.setName("United States");
        testCountry.setCode("US");
        testCountry = countryRepository.save(testCountry);
    }

    @Test
    void testShowCityList() throws Exception {
        // Given
        City city1 = new City();
        city1.setName("New York");
        city1.setCountry(testCountry);
        cityRepository.save(city1);

        City city2 = new City();
        city2.setName("Los Angeles");
        city2.setCountry(testCountry);
        cityRepository.save(city2);

        // When & Then
        mockMvc.perform(get("/web/cities"))
                .andExpect(status().isOk())
                .andExpect(view().name("cities/list"))
                .andExpect(model().attributeExists("cities"));
    }

    @Test
    void testShowAddCityForm() throws Exception {
        // When & Then
        mockMvc.perform(get("/web/cities/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("cities/form"))
                .andExpect(model().attributeExists("cityDTO"))
                .andExpect(model().attributeExists("countries"));
    }

    @Test
    void testShowEditCityForm() throws Exception {
        // Given
        City city = new City();
        city.setName("Chicago");
        city.setCountry(testCountry);
        City saved = cityRepository.save(city);

        // When & Then
        mockMvc.perform(get("/web/cities/{id}/edit", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("cities/form"))
                .andExpect(model().attributeExists("cityDTO"))
                .andExpect(model().attributeExists("countries"));
    }

    @Test
    void testShowCityDetails() throws Exception {
        // Given
        City city = new City();
        city.setName("Miami");
        city.setCountry(testCountry);
        City saved = cityRepository.save(city);

        // When & Then
        mockMvc.perform(get("/web/cities/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("cities/details"))
                .andExpect(model().attributeExists("city"));
    }

    @Test
    void testCreateCitySuccess() throws Exception {
        // When & Then
        mockMvc.perform(post("/web/cities")
                .param("name", "Seattle")
                .param("countryId", testCountry.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/cities"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify city was created
        assertThat(cityRepository.findAll())
                .hasSize(1)
                .extracting("name")
                .contains("Seattle");

        City fromDb = cityRepository.findAll().get(0);
        assertThat(fromDb.getCountry().getId()).isEqualTo(testCountry.getId());
    }

    @Test
    void testCreateCityValidationError() throws Exception {
        // When & Then - Missing required fields
        mockMvc.perform(post("/web/cities")
                .param("name", "") // Empty name
                .param("countryId", testCountry.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("cities/form"))
                .andExpect(model().attributeExists("cityDTO"))
                .andExpect(model().attributeExists("countries"))
                .andExpect(model().hasErrors());

        // Verify no city was created
        assertThat(cityRepository.findAll()).isEmpty();
    }

    @Test
    void testUpdateCitySuccess() throws Exception {
        // Given
        City existingCity = new City();
        existingCity.setName("Houston");
        existingCity.setCountry(testCountry);
        City saved = cityRepository.save(existingCity);

        // When & Then
        mockMvc.perform(post("/web/cities/{id}", saved.getId())
                .param("name", "Houston-Updated")
                .param("countryId", testCountry.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/cities"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify city was updated
        City fromDb = cityRepository.findById(saved.getId()).orElse(null);
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getName()).isEqualTo("Houston-Updated");
    }

    @Test
    void testDeleteCitySuccess() throws Exception {
        // Given
        City city = new City();
        city.setName("Phoenix");
        city.setCountry(testCountry);
        City saved = cityRepository.save(city);

        // When & Then
        mockMvc.perform(post("/web/cities/{id}/delete", saved.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/cities"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify city was deleted
        assertThat(cityRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testEditCityNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/web/cities/{id}/edit", 9999))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/cities"));
    }

    @Test
    void testDeleteCityNotFound() throws Exception {
        // When & Then - Should still redirect with success (idempotent delete)
        mockMvc.perform(post("/web/cities/{id}/delete", 9999))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/cities"))
                .andExpect(flash().attributeExists("successMessage"));
    }
}
