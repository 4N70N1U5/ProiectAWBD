package com.antonio.skybase.controllers;

import com.antonio.skybase.entities.Country;
import com.antonio.skybase.exceptions.BadRequestException;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.services.CountryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CountryMvcControllerTest {

    @Mock
    private CountryService countryService;

    @InjectMocks
    private CountryMvcController countryMvcController;

    private MockMvc mockMvc;

    private Country testCountry;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(countryMvcController).build();

        // Set up test data
        testCountry = new Country();
        testCountry.setId(1);
        testCountry.setName("United States");
        testCountry.setCode("US");
    }

    @Test
    void testGetAllCountries() throws Exception {
        // Given
        List<Country> countries = Arrays.asList(testCountry);
        when(countryService.getAll()).thenReturn(countries);

        // When & Then
        mockMvc.perform(get("/web/countries"))
                .andExpect(status().isOk())
                .andExpect(view().name("countries/list"))
                .andExpect(model().attributeExists("countries"))
                .andExpect(model().attribute("countries", countries));

        verify(countryService).getAll();
    }

    @Test
    void testShowCreateForm() throws Exception {
        // When & Then
        mockMvc.perform(get("/web/countries/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("countries/form"))
                .andExpect(model().attributeExists("country"));
    }

    @Test
    void testCreateCountrySuccess() throws Exception {
        // Given
        when(countryService.create(any(Country.class))).thenReturn(testCountry);

        // When & Then
        mockMvc.perform(post("/web/countries")
                .param("name", "United States")
                .param("code", "US"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/countries"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(countryService).create(any(Country.class));
    }

    @Test
    void testCreateCountryValidationErrors() throws Exception {
        // When & Then - Submit form with validation errors (empty name)
        mockMvc.perform(post("/web/countries")
                .param("name", "") // Empty name
                .param("code", "US"))
                .andExpect(status().isOk())
                .andExpect(view().name("countries/form"))
                .andExpect(model().attributeExists("country"));

        verify(countryService, never()).create(any(Country.class));
    }

    @Test
    void testCreateCountryServiceException() throws Exception {
        // Given
        when(countryService.create(any(Country.class)))
                .thenThrow(new BadRequestException("Country code already exists"));

        // When & Then
        mockMvc.perform(post("/web/countries")
                .param("name", "United States")
                .param("code", "US"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/countries"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(countryService).create(any(Country.class));
    }

    @Test
    void testGetCountryById() throws Exception {
        // Given
        when(countryService.getById(1)).thenReturn(testCountry);

        // When & Then
        mockMvc.perform(get("/web/countries/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("countries/details"))
                .andExpect(model().attributeExists("country"))
                .andExpect(model().attribute("country", testCountry));

        verify(countryService).getById(1);
    }

    @Test
    void testGetCountryByIdNotFound() throws Exception {
        // Given
        when(countryService.getById(1)).thenThrow(new NotFoundException("Country not found"));
        when(countryService.getAll()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/web/countries/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("countries/list"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attributeExists("countries"));

        verify(countryService).getById(1);
        verify(countryService).getAll();
    }

    @Test
    void testShowEditForm() throws Exception {
        // Given
        when(countryService.getById(1)).thenReturn(testCountry);

        // When & Then
        mockMvc.perform(get("/web/countries/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("countries/form"))
                .andExpect(model().attributeExists("country"));

        verify(countryService).getById(1);
    }

    @Test
    void testShowEditFormNotFound() throws Exception {
        // Given
        when(countryService.getById(1)).thenThrow(new NotFoundException("Country not found"));

        // When & Then
        mockMvc.perform(get("/web/countries/1/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/countries"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(countryService).getById(1);
    }

    @Test
    void testUpdateCountrySuccess() throws Exception {
        // Given
        when(countryService.update(eq(1), any(Country.class))).thenReturn(testCountry);

        // When & Then
        mockMvc.perform(post("/web/countries/1")
                .param("name", "United States")
                .param("code", "US"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/countries"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(countryService).update(eq(1), any(Country.class));
    }

    @Test
    void testUpdateCountryValidationErrors() throws Exception {
        // When & Then - Submit form with validation errors
        mockMvc.perform(post("/web/countries/1")
                .param("name", "") // Empty name
                .param("code", "US"))
                .andExpect(status().isOk())
                .andExpect(view().name("countries/form"))
                .andExpect(model().attributeExists("country"));

        verify(countryService, never()).update(anyInt(), any(Country.class));
    }

    @Test
    void testUpdateCountryServiceException() throws Exception {
        // Given
        when(countryService.update(eq(1), any(Country.class)))
                .thenThrow(new BadRequestException("Invalid country data"));

        // When & Then
        mockMvc.perform(post("/web/countries/1")
                .param("name", "United States")
                .param("code", "US"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/countries"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(countryService).update(eq(1), any(Country.class));
    }

    @Test
    void testDeleteCountrySuccess() throws Exception {
        // Given
        doNothing().when(countryService).delete(1);

        // When & Then
        mockMvc.perform(post("/web/countries/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/countries"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(countryService).delete(1);
    }

    @Test
    void testDeleteCountryServiceException() throws Exception {
        // Given
        doThrow(new BadRequestException("Cannot delete country with existing cities")).when(countryService).delete(1);

        // When & Then
        mockMvc.perform(post("/web/countries/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/countries"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(countryService).delete(1);
    }
}
