package com.antonio.skybase.controllers;

import com.antonio.skybase.config.TestConfig;
import com.antonio.skybase.entities.Country;
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
@Import(TestConfig.class)
@ActiveProfiles("test")
@Transactional
class CountryMvcControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CountryRepository countryRepository;

    private MockMvc mockMvc;
    private Country testCountry;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        countryRepository.deleteAll();

        testCountry = new Country();
        testCountry.setName("Test Country");
        testCountry.setCode("TC");
    }

    @Test
    void testListCountriesPage() throws Exception {
        // Given
        countryRepository.save(testCountry);

        // When & Then
        mockMvc.perform(get("/web/countries"))
                .andExpect(status().isOk())
                .andExpect(view().name("countries/list"))
                .andExpect(model().attributeExists("countries"));
    }

    @Test
    void testCreateCountrySuccess() throws Exception {
        // When & Then
        mockMvc.perform(post("/web/countries")
                .param("name", "New Country")
                .param("code", "NC"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/countries"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify country was created
        assertThat(countryRepository.existsByCode("NC")).isTrue();
    }

    @Test
    void testShowCountryDetails() throws Exception {
        // Given
        Country savedCountry = countryRepository.save(testCountry);

        // When & Then
        mockMvc.perform(get("/web/countries/{id}", savedCountry.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("countries/details"))
                .andExpect(model().attributeExists("country"));
    }
}
