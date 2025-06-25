package com.antonio.skybase.controllers;

import com.antonio.skybase.config.TestConfig;
import com.antonio.skybase.entities.Airport;
import com.antonio.skybase.entities.City;
import com.antonio.skybase.entities.Country;
import com.antonio.skybase.repositories.AirportRepository;
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
public class AirportMvcControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CountryRepository countryRepository;

    private MockMvc mockMvc;

    private City testCity;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Clean up data before each test
        airportRepository.deleteAll();
        cityRepository.deleteAll();
        countryRepository.deleteAll();

        // Set up required data
        Country country = new Country();
        country.setName("United States");
        country.setCode("US");
        country = countryRepository.save(country);

        testCity = new City();
        testCity.setName("Los Angeles");
        testCity.setCountry(country);
        testCity = cityRepository.save(testCity);
    }

    @Test
    void testShowAirportList() throws Exception {
        // Given
        Airport airport1 = new Airport();
        airport1.setCode("LAX");
        airport1.setName("Los Angeles International");
        airport1.setCity(testCity);
        airportRepository.save(airport1);

        Airport airport2 = new Airport();
        airport2.setCode("BUR");
        airport2.setName("Hollywood Burbank Airport");
        airport2.setCity(testCity);
        airportRepository.save(airport2);

        // When & Then
        mockMvc.perform(get("/web/airports"))
                .andExpect(status().isOk())
                .andExpect(view().name("airports/list"))
                .andExpect(model().attributeExists("airports"));
    }

    @Test
    void testShowAddAirportForm() throws Exception {
        // When & Then
        mockMvc.perform(get("/web/airports/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("airports/form"))
                .andExpect(model().attributeExists("airportDTO"))
                .andExpect(model().attributeExists("cities"));
    }

    @Test
    void testShowEditAirportForm() throws Exception {
        // Given
        Airport airport = new Airport();
        airport.setCode("SNA");
        airport.setName("John Wayne Airport");
        airport.setCity(testCity);
        Airport saved = airportRepository.save(airport);

        // When & Then
        mockMvc.perform(get("/web/airports/{id}/edit", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("airports/form"))
                .andExpect(model().attributeExists("airportDTO"))
                .andExpect(model().attributeExists("cities"));
    }

    @Test
    void testShowAirportDetails() throws Exception {
        // Given
        Airport airport = new Airport();
        airport.setCode("LGB");
        airport.setName("Long Beach Airport");
        airport.setCity(testCity);
        Airport saved = airportRepository.save(airport);

        // When & Then
        mockMvc.perform(get("/web/airports/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("airports/details"))
                .andExpect(model().attributeExists("airport"));
    }

    @Test
    void testCreateAirportSuccess() throws Exception {
        // When & Then
        mockMvc.perform(post("/web/airports")
                .param("code", "SMO")
                .param("name", "Santa Monica Airport")
                .param("cityId", testCity.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/airports"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify airport was created
        assertThat(airportRepository.findAll())
                .hasSize(1)
                .extracting("code")
                .contains("SMO");

        Airport fromDb = airportRepository.findAll().get(0);
        assertThat(fromDb.getName()).isEqualTo("Santa Monica Airport");
        assertThat(fromDb.getCity().getId()).isEqualTo(testCity.getId());
    }

    @Test
    void testCreateAirportValidationError() throws Exception {
        // When & Then - Missing required fields
        mockMvc.perform(post("/web/airports")
                .param("code", "") // Empty code
                .param("name", "") // Empty name
                .param("cityId", testCity.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("airports/form"))
                .andExpect(model().attributeExists("airportDTO"))
                .andExpect(model().attributeExists("cities"))
                .andExpect(model().hasErrors());

        // Verify no airport was created
        assertThat(airportRepository.findAll()).isEmpty();
    }

    @Test
    void testUpdateAirportSuccess() throws Exception {
        // Given
        Airport existingAirport = new Airport();
        existingAirport.setCode("VNY");
        existingAirport.setName("Van Nuys Airport");
        existingAirport.setCity(testCity);
        Airport saved = airportRepository.save(existingAirport);

        // When & Then
        mockMvc.perform(post("/web/airports/{id}", saved.getId())
                .param("code", "VNY")
                .param("name", "Van Nuys Airport - Updated")
                .param("cityId", testCity.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/airports"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify airport was updated
        Airport fromDb = airportRepository.findById(saved.getId()).orElse(null);
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getName()).isEqualTo("Van Nuys Airport - Updated");
    }

    @Test
    void testDeleteAirportSuccess() throws Exception {
        // Given
        Airport airport = new Airport();
        airport.setCode("HHR");
        airport.setName("Hawthorne Municipal Airport");
        airport.setCity(testCity);
        Airport saved = airportRepository.save(airport);

        // When & Then
        mockMvc.perform(post("/web/airports/{id}/delete", saved.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/airports"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify airport was deleted
        assertThat(airportRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testShowAirportNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/web/airports/{id}", 9999))
                .andExpect(status().isOk())
                .andExpect(view().name("airports/list"))
                .andExpect(model().attributeExists("airports"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void testEditAirportNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/web/airports/{id}/edit", 9999))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/airports"));
    }

    @Test
    void testDeleteAirportNotFound() throws Exception {
        // When & Then - Should still redirect with success (idempotent delete)
        mockMvc.perform(post("/web/airports/{id}/delete", 9999))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/airports"))
                .andExpect(flash().attributeExists("successMessage"));
    }
}
