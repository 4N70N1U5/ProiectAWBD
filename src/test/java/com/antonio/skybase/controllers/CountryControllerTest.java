package com.antonio.skybase.controllers;

import com.antonio.skybase.entities.Country;
import com.antonio.skybase.services.CountryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CountryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CountryService countryService;

    @InjectMocks
    private CountryController countryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(countryController).build();
    }

    @Test
    void testCreateCountry() throws Exception {
        Country country = new Country();
        country.setId(1);
        country.setName("Test Country");
        country.setCode("TC");

        when(countryService.create(any(Country.class))).thenReturn(country);

        mockMvc.perform(post("/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test Country\", \"code\": \"TC\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Country"))
                .andExpect(jsonPath("$.code").value("TC"));
    }

    @Test
    void testCreateCountryWithBlankName() throws Exception {
        mockMvc.perform(post("/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\", \"code\": \"TC\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateCountryWithBlankCode() throws Exception {
        mockMvc.perform(post("/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test Country\", \"code\": \"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateCountryWithInvalidCodeSize() throws Exception {
        mockMvc.perform(post("/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test Country\", \"code\": \"T\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllCountries() throws Exception {
        Country country1 = new Country();
        country1.setId(1);
        country1.setName("Country 1");
        country1.setCode("C1");

        Country country2 = new Country();
        country2.setId(2);
        country2.setName("Country 2");
        country2.setCode("C2");

        List<Country> countries = Arrays.asList(country1, country2);

        when(countryService.getAll()).thenReturn(countries);

        mockMvc.perform(get("/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Country 1"))
                .andExpect(jsonPath("$[0].code").value("C1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Country 2"))
                .andExpect(jsonPath("$[1].code").value("C2"));
    }

    @Test
    void testGetCountryById() throws Exception {
        Country country = new Country();
        country.setId(1);
        country.setName("Test Country");
        country.setCode("TC");

        when(countryService.getById(anyInt())).thenReturn(country);

        mockMvc.perform(get("/countries/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Country"))
                .andExpect(jsonPath("$.code").value("TC"));
    }

    @Test
    void testUpdateCountry() throws Exception {
        Country country = new Country();
        country.setId(1);
        country.setName("Updated Country");
        country.setCode("UC");

        when(countryService.update(anyInt(), any(Country.class))).thenReturn(country);

        mockMvc.perform(put("/countries/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Updated Country\", \"code\": \"UC\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Country"))
                .andExpect(jsonPath("$.code").value("UC"));
    }

    @Test
    void testDeleteCountry() throws Exception {
        mockMvc.perform(delete("/countries/1"))
                .andExpect(status().isNoContent());
    }
}