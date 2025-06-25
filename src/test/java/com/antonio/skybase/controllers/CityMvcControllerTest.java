package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.CityDTO;
import com.antonio.skybase.entities.City;
import com.antonio.skybase.entities.Country;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.services.CityService;
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
class CityMvcControllerTest {

    @Mock
    private CityService cityService;

    @Mock
    private CountryService countryService;

    @InjectMocks
    private CityMvcController cityMvcController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cityMvcController).build();
    }

    @Test
    void getAllCities_ShouldReturnListView() throws Exception {
        // Arrange
        City city1 = new City();
        city1.setId(1);
        city1.setName("New York");

        City city2 = new City();
        city2.setId(2);
        city2.setName("Los Angeles");

        List<City> cities = Arrays.asList(city1, city2);
        when(cityService.getAll()).thenReturn(cities);

        // Act & Assert
        mockMvc.perform(get("/web/cities"))
                .andExpect(status().isOk())
                .andExpect(view().name("cities/list"))
                .andExpect(model().attributeExists("cities"))
                .andExpect(model().attribute("cities", cities));

        verify(cityService).getAll();
    }

    @Test
    void showCreateForm_ShouldReturnFormView() throws Exception {
        // Arrange
        Country country = new Country();
        country.setId(1);
        country.setName("USA");
        List<Country> countries = Arrays.asList(country);
        when(countryService.getAll()).thenReturn(countries);

        // Act & Assert
        mockMvc.perform(get("/web/cities/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("cities/form"))
                .andExpect(model().attributeExists("cityDTO"))
                .andExpect(model().attributeExists("countries"))
                .andExpect(model().attribute("countries", countries));

        verify(countryService).getAll();
    }

    @Test
    void createCity_WithValidData_ShouldRedirectToList() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/web/cities")
                .param("name", "New York")
                .param("countryId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/cities"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(cityService).create(any(CityDTO.class));
    }

    @Test
    void createCity_WithInvalidData_ShouldReturnFormView() throws Exception {
        // Arrange
        Country country = new Country();
        country.setId(1);
        country.setName("USA");
        List<Country> countries = Arrays.asList(country);
        when(countryService.getAll()).thenReturn(countries);

        // Act & Assert
        mockMvc.perform(post("/web/cities")
                .param("name", "") // Empty name should trigger validation error
                .param("countryId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("cities/form"))
                .andExpect(model().attributeExists("cityDTO"))
                .andExpect(model().attributeExists("countries"))
                .andExpect(model().hasErrors());

        verify(countryService).getAll();
        verifyNoInteractions(cityService);
    }

    @Test
    void createCity_ServiceThrowsException_ShouldRedirectWithErrorMessage() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Database error")).when(cityService).create(any(CityDTO.class));

        // Act & Assert
        mockMvc.perform(post("/web/cities")
                .param("name", "New York")
                .param("countryId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/cities"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(cityService).create(any(CityDTO.class));
    }

    @Test
    void getCityById_WithValidId_ShouldReturnDetailsView() throws Exception {
        // Arrange
        Country country = new Country();
        country.setId(1);
        country.setName("USA");

        City city = new City();
        city.setId(1);
        city.setName("New York");
        city.setCountry(country);
        when(cityService.getById(1)).thenReturn(city);

        // Act & Assert
        mockMvc.perform(get("/web/cities/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("cities/details"))
                .andExpect(model().attributeExists("city"))
                .andExpect(model().attribute("city", city));

        verify(cityService).getById(1);
    }

    @Test
    void showEditForm_WithValidId_ShouldReturnFormView() throws Exception {
        // Arrange
        Country country = new Country();
        country.setId(1);
        country.setName("USA");

        City city = new City();
        city.setId(1);
        city.setName("New York");
        city.setCountry(country);

        List<Country> countries = Arrays.asList(country);

        when(cityService.getById(1)).thenReturn(city);
        when(countryService.getAll()).thenReturn(countries);

        // Act & Assert
        mockMvc.perform(get("/web/cities/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("cities/form"))
                .andExpect(model().attributeExists("cityDTO"))
                .andExpect(model().attributeExists("cityId"))
                .andExpect(model().attributeExists("countries"))
                .andExpect(model().attribute("countries", countries));

        verify(cityService).getById(1);
        verify(countryService).getAll();
    }

    @Test
    void showEditForm_WithInvalidId_ShouldRedirectToList() throws Exception {
        // Arrange
        when(cityService.getById(999)).thenThrow(new NotFoundException("City not found"));

        // Act & Assert
        mockMvc.perform(get("/web/cities/999/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/cities"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(cityService).getById(999);
        verifyNoInteractions(countryService);
    }

    @Test
    void updateCity_WithValidData_ShouldRedirectToList() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/web/cities/1")
                .param("name", "Updated New York")
                .param("countryId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/cities"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(cityService).update(eq(1), any(CityDTO.class));
    }

    @Test
    void updateCity_WithInvalidData_ShouldReturnFormView() throws Exception {
        // Arrange
        Country country = new Country();
        country.setId(1);
        country.setName("USA");
        List<Country> countries = Arrays.asList(country);
        when(countryService.getAll()).thenReturn(countries);

        // Act & Assert
        mockMvc.perform(post("/web/cities/1")
                .param("name", "") // Empty name should trigger validation error
                .param("countryId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("cities/form"))
                .andExpect(model().attributeExists("cityDTO"))
                .andExpect(model().attributeExists("countries"))
                .andExpect(model().attributeExists("cityId"))
                .andExpect(model().hasErrors());

        verify(countryService).getAll();
        verifyNoInteractions(cityService);
    }

    @Test
    void updateCity_ServiceThrowsException_ShouldRedirectWithErrorMessage() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Update failed")).when(cityService).update(eq(1), any(CityDTO.class));

        // Act & Assert
        mockMvc.perform(post("/web/cities/1")
                .param("name", "Updated New York")
                .param("countryId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/cities"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(cityService).update(eq(1), any(CityDTO.class));
    }

    @Test
    void deleteCity_WithValidId_ShouldRedirectToList() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/web/cities/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/cities"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(cityService).delete(1);
    }

    @Test
    void deleteCity_ServiceThrowsException_ShouldRedirectWithErrorMessage() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Delete failed")).when(cityService).delete(1);

        // Act & Assert
        mockMvc.perform(post("/web/cities/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/cities"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(cityService).delete(1);
    }
}
