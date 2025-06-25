package com.antonio.skybase.controllers;

import com.antonio.skybase.entities.Aircraft;
import com.antonio.skybase.exceptions.BadRequestException;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.services.AircraftService;
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
class AircraftMvcControllerTest {

    @Mock
    private AircraftService aircraftService;

    @InjectMocks
    private AircraftMvcController aircraftMvcController;

    private MockMvc mockMvc;

    private Aircraft testAircraft;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(aircraftMvcController).build();

        // Set up test data
        testAircraft = new Aircraft();
        testAircraft.setId(1);
        testAircraft.setRegistration("N123AB");
        testAircraft.setType("Boeing 737");
        testAircraft.setRange(3000);
        testAircraft.setCapacity(150);
    }

    @Test
    void testGetAllAircraft() throws Exception {
        // Given
        List<Aircraft> aircraft = Arrays.asList(testAircraft);
        when(aircraftService.getAll()).thenReturn(aircraft);

        // When & Then
        mockMvc.perform(get("/web/aircraft"))
                .andExpect(status().isOk())
                .andExpect(view().name("aircraft/list"))
                .andExpect(model().attributeExists("aircraft"))
                .andExpect(model().attribute("aircraft", aircraft));

        verify(aircraftService).getAll();
    }

    @Test
    void testShowCreateForm() throws Exception {
        // When & Then
        mockMvc.perform(get("/web/aircraft/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("aircraft/form"))
                .andExpect(model().attributeExists("aircraft"));
    }

    @Test
    void testCreateAircraftSuccess() throws Exception {
        // Given
        when(aircraftService.create(any(Aircraft.class))).thenReturn(testAircraft);

        // When & Then
        mockMvc.perform(post("/web/aircraft")
                .param("registration", "N123AB")
                .param("type", "Boeing 737")
                .param("range", "3000")
                .param("capacity", "150"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/aircraft"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(aircraftService).create(any(Aircraft.class));
    }

    @Test
    void testCreateAircraftValidationErrors() throws Exception {
        // When & Then - Submit form with validation errors (empty registration)
        mockMvc.perform(post("/web/aircraft")
                .param("registration", "") // Empty registration
                .param("type", "Boeing 737")
                .param("range", "3000")
                .param("capacity", "150"))
                .andExpect(status().isOk())
                .andExpect(view().name("aircraft/form"))
                .andExpect(model().attributeExists("aircraft"));

        verify(aircraftService, never()).create(any(Aircraft.class));
    }

    @Test
    void testCreateAircraftServiceException() throws Exception {
        // Given
        when(aircraftService.create(any(Aircraft.class)))
                .thenThrow(new BadRequestException("Aircraft registration already exists"));

        // When & Then
        mockMvc.perform(post("/web/aircraft")
                .param("registration", "N123AB")
                .param("type", "Boeing 737")
                .param("range", "3000")
                .param("capacity", "150"))
                .andExpect(status().isOk())
                .andExpect(view().name("aircraft/form"))
                .andExpect(model().attributeExists("aircraft"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(aircraftService).create(any(Aircraft.class));
    }

    @Test
    void testGetAircraftById() throws Exception {
        // Given
        when(aircraftService.getById(1)).thenReturn(testAircraft);

        // When & Then
        mockMvc.perform(get("/web/aircraft/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("aircraft/details"))
                .andExpect(model().attributeExists("aircraft"))
                .andExpect(model().attribute("aircraft", testAircraft));

        verify(aircraftService).getById(1);
    }

    @Test
    void testGetAircraftByIdNotFound() throws Exception {
        // Given
        when(aircraftService.getById(1)).thenThrow(new NotFoundException("Aircraft not found"));
        when(aircraftService.getAll()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/web/aircraft/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("aircraft/list"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attributeExists("aircraft"));

        verify(aircraftService).getById(1);
        verify(aircraftService).getAll();
    }

    @Test
    void testShowEditForm() throws Exception {
        // Given
        when(aircraftService.getById(1)).thenReturn(testAircraft);

        // When & Then
        mockMvc.perform(get("/web/aircraft/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("aircraft/form"))
                .andExpect(model().attributeExists("aircraft"));

        verify(aircraftService).getById(1);
    }

    @Test
    void testUpdateAircraftSuccess() throws Exception {
        // Given
        when(aircraftService.update(eq(1), any(Aircraft.class))).thenReturn(testAircraft);

        // When & Then
        mockMvc.perform(post("/web/aircraft/1")
                .param("registration", "N123AB")
                .param("type", "Boeing 737")
                .param("range", "3000")
                .param("capacity", "150"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/aircraft"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(aircraftService).update(eq(1), any(Aircraft.class));
    }

    @Test
    void testUpdateAircraftValidationErrors() throws Exception {
        // When & Then - Submit form with validation errors
        mockMvc.perform(post("/web/aircraft/1")
                .param("registration", "") // Empty registration
                .param("type", "Boeing 737")
                .param("range", "3000")
                .param("capacity", "150"))
                .andExpect(status().isOk())
                .andExpect(view().name("aircraft/form"))
                .andExpect(model().attributeExists("aircraft"));

        verify(aircraftService, never()).update(anyInt(), any(Aircraft.class));
    }

    @Test
    void testUpdateAircraftServiceException() throws Exception {
        // Given
        when(aircraftService.update(eq(1), any(Aircraft.class)))
                .thenThrow(new BadRequestException("Invalid aircraft data"));

        // When & Then
        mockMvc.perform(post("/web/aircraft/1")
                .param("registration", "N123AB")
                .param("type", "Boeing 737")
                .param("range", "3000")
                .param("capacity", "150"))
                .andExpect(status().isOk())
                .andExpect(view().name("aircraft/form"))
                .andExpect(model().attributeExists("aircraft"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(aircraftService).update(eq(1), any(Aircraft.class));
    }

    @Test
    void testDeleteAircraftSuccess() throws Exception {
        // Given
        doNothing().when(aircraftService).delete(1);

        // When & Then
        mockMvc.perform(post("/web/aircraft/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/aircraft"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(aircraftService).delete(1);
    }

    @Test
    void testDeleteAircraftServiceException() throws Exception {
        // Given
        doThrow(new BadRequestException("Cannot delete aircraft with existing assignments")).when(aircraftService)
                .delete(1);

        // When & Then
        mockMvc.perform(post("/web/aircraft/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/aircraft"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(aircraftService).delete(1);
    }
}
