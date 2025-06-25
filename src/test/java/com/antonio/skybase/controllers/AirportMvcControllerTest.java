package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.AirportDTO;
import com.antonio.skybase.entities.Airport;
import com.antonio.skybase.entities.City;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.services.AirportService;
import com.antonio.skybase.services.CityService;
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
class AirportMvcControllerTest {

    @Mock
    private AirportService airportService;

    @Mock
    private CityService cityService;

    @InjectMocks
    private AirportMvcController airportMvcController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(airportMvcController).build();
    }

    @Test
    void list_ShouldReturnListView() throws Exception {
        // Arrange
        Airport airport1 = new Airport();
        airport1.setId(1);
        airport1.setName("John F. Kennedy International Airport");
        airport1.setCode("JFK");

        Airport airport2 = new Airport();
        airport2.setId(2);
        airport2.setName("Los Angeles International Airport");
        airport2.setCode("LAX");

        List<Airport> airports = Arrays.asList(airport1, airport2);
        when(airportService.getAll()).thenReturn(airports);

        // Act & Assert
        mockMvc.perform(get("/web/airports"))
                .andExpect(status().isOk())
                .andExpect(view().name("airports/list"))
                .andExpect(model().attributeExists("airports"))
                .andExpect(model().attribute("airports", airports));

        verify(airportService).getAll();
    }

    @Test
    void showCreateForm_ShouldReturnFormView() throws Exception {
        // Arrange
        City city = new City();
        city.setId(1);
        city.setName("New York");
        List<City> cities = Arrays.asList(city);
        when(cityService.getAll()).thenReturn(cities);

        // Act & Assert
        mockMvc.perform(get("/web/airports/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("airports/form"))
                .andExpect(model().attributeExists("airportDTO"))
                .andExpect(model().attributeExists("cities"))
                .andExpect(model().attribute("cities", cities));

        verify(cityService).getAll();
    }

    @Test
    void create_WithValidData_ShouldRedirectToList() throws Exception {
        // Arrange
        Airport airport = new Airport();
        airport.setId(1);
        airport.setName("John F. Kennedy International Airport");
        airport.setCode("JFK");
        when(airportService.create(any(AirportDTO.class))).thenReturn(airport);

        // Act & Assert
        mockMvc.perform(post("/web/airports")
                .param("name", "John F. Kennedy International Airport")
                .param("code", "JFK")
                .param("cityId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/airports"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(airportService).create(any(AirportDTO.class));
    }

    @Test
    void create_WithInvalidData_ShouldReturnFormView() throws Exception {
        // Arrange
        City city = new City();
        city.setId(1);
        city.setName("New York");
        List<City> cities = Arrays.asList(city);
        when(cityService.getAll()).thenReturn(cities);

        // Act & Assert
        mockMvc.perform(post("/web/airports")
                .param("name", "") // Empty name should trigger validation error
                .param("code", "JFK")
                .param("cityId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("airports/form"))
                .andExpect(model().attributeExists("airportDTO"))
                .andExpect(model().attributeExists("cities"))
                .andExpect(model().hasErrors());

        verify(cityService).getAll();
        verifyNoInteractions(airportService);
    }

    @Test
    void create_ServiceThrowsException_ShouldReturnFormWithErrorMessage() throws Exception {
        // Arrange
        City city = new City();
        city.setId(1);
        city.setName("New York");
        List<City> cities = Arrays.asList(city);
        when(cityService.getAll()).thenReturn(cities);
        doThrow(new RuntimeException("Database error")).when(airportService).create(any(AirportDTO.class));

        // Act & Assert
        mockMvc.perform(post("/web/airports")
                .param("name", "John F. Kennedy International Airport")
                .param("code", "JFK")
                .param("cityId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("airports/form"))
                .andExpect(model().attributeExists("airportDTO"))
                .andExpect(model().attributeExists("cities"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(airportService).create(any(AirportDTO.class));
        verify(cityService).getAll();
    }

    @Test
    void details_WithValidId_ShouldReturnDetailsView() throws Exception {
        // Arrange
        City city = new City();
        city.setId(1);
        city.setName("New York");

        Airport airport = new Airport();
        airport.setId(1);
        airport.setName("John F. Kennedy International Airport");
        airport.setCode("JFK");
        airport.setCity(city);
        when(airportService.getById(1)).thenReturn(airport);

        // Act & Assert
        mockMvc.perform(get("/web/airports/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("airports/details"))
                .andExpect(model().attributeExists("airport"))
                .andExpect(model().attribute("airport", airport));

        verify(airportService).getById(1);
    }

    @Test
    void showEditForm_WithValidId_ShouldReturnFormView() throws Exception {
        // Arrange
        City city = new City();
        city.setId(1);
        city.setName("New York");

        Airport airport = new Airport();
        airport.setId(1);
        airport.setName("John F. Kennedy International Airport");
        airport.setCode("JFK");
        airport.setCity(city);

        List<City> cities = Arrays.asList(city);

        when(airportService.getById(1)).thenReturn(airport);
        when(cityService.getAll()).thenReturn(cities);

        // Act & Assert
        mockMvc.perform(get("/web/airports/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("airports/form"))
                .andExpect(model().attributeExists("airportDTO"))
                .andExpect(model().attributeExists("cities"))
                .andExpect(model().attributeExists("airportId"))
                .andExpect(model().attribute("cities", cities));

        verify(airportService).getById(1);
        verify(cityService).getAll();
    }

    @Test
    void showEditForm_WithInvalidId_ShouldRedirectToList() throws Exception {
        // Arrange
        when(airportService.getById(999)).thenThrow(new NotFoundException("Airport not found"));

        // Act & Assert
        mockMvc.perform(get("/web/airports/999/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/airports"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(airportService).getById(999);
        verifyNoInteractions(cityService);
    }

    @Test
    void update_WithValidData_ShouldRedirectToList() throws Exception {
        // Arrange
        Airport airport = new Airport();
        airport.setId(1);
        airport.setName("John F. Kennedy International Airport");
        airport.setCode("JFK");
        when(airportService.update(eq(1), any(AirportDTO.class))).thenReturn(airport);

        // Act & Assert
        mockMvc.perform(post("/web/airports/1")
                .param("id", "1")
                .param("name", "John F. Kennedy International Airport")
                .param("code", "JFK")
                .param("cityId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/airports"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(airportService).update(eq(1), any(AirportDTO.class));
    }

    @Test
    void update_WithInvalidData_ShouldReturnFormView() throws Exception {
        // Arrange
        City city = new City();
        city.setId(1);
        city.setName("New York");
        List<City> cities = Arrays.asList(city);
        when(cityService.getAll()).thenReturn(cities);

        // Act & Assert
        mockMvc.perform(post("/web/airports/1")
                .param("id", "1")
                .param("name", "") // Empty name should trigger validation error
                .param("code", "JFK")
                .param("cityId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("airports/form"))
                .andExpect(model().attributeExists("airportDTO"))
                .andExpect(model().attributeExists("cities"))
                .andExpect(model().attributeExists("airportId"))
                .andExpect(model().hasErrors());

        verify(cityService).getAll();
        verifyNoInteractions(airportService);
    }

    @Test
    void update_ServiceThrowsException_ShouldReturnFormWithErrorMessage() throws Exception {
        // Arrange
        City city = new City();
        city.setId(1);
        city.setName("New York");
        List<City> cities = Arrays.asList(city);
        when(cityService.getAll()).thenReturn(cities);
        doThrow(new RuntimeException("Update failed")).when(airportService).update(eq(1), any(AirportDTO.class));

        // Act & Assert
        mockMvc.perform(post("/web/airports/1")
                .param("id", "1")
                .param("name", "John F. Kennedy International Airport")
                .param("code", "JFK")
                .param("cityId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("airports/form"))
                .andExpect(model().attributeExists("airportDTO"))
                .andExpect(model().attributeExists("cities"))
                .andExpect(model().attributeExists("airportId"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(airportService).update(eq(1), any(AirportDTO.class));
        verify(cityService).getAll();
    }

    @Test
    void delete_WithValidId_ShouldRedirectToList() throws Exception {
        // Arrange
        Airport airport = new Airport();
        airport.setId(1);
        airport.setName("John F. Kennedy International Airport");
        airport.setCode("JFK");
        when(airportService.getById(1)).thenReturn(airport);

        // Act & Assert
        mockMvc.perform(post("/web/airports/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/airports"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(airportService).getById(1);
        verify(airportService).delete(1);
    }
}
