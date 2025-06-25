package com.antonio.skybase.controllers;

import com.antonio.skybase.config.TestConfig;
import com.antonio.skybase.entities.Aircraft;
import com.antonio.skybase.repositories.AircraftRepository;
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
public class AircraftMvcControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AircraftRepository aircraftRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Clean up data before each test
        aircraftRepository.deleteAll();
    }

    @Test
    void testShowAircraftList() throws Exception {
        // Given
        Aircraft aircraft1 = new Aircraft();
        aircraft1.setRegistration("N123AB");
        aircraft1.setType("Boeing 737");
        aircraft1.setRange(3000);
        aircraft1.setCapacity(150);
        aircraftRepository.save(aircraft1);

        Aircraft aircraft2 = new Aircraft();
        aircraft2.setRegistration("N456CD");
        aircraft2.setType("Airbus A320");
        aircraft2.setRange(3500);
        aircraft2.setCapacity(180);
        aircraftRepository.save(aircraft2);

        // When & Then
        mockMvc.perform(get("/web/aircraft"))
                .andExpect(status().isOk())
                .andExpect(view().name("aircraft/list"))
                .andExpect(model().attributeExists("aircraft"));
    }

    @Test
    void testShowAddAircraftForm() throws Exception {
        // When & Then
        mockMvc.perform(get("/web/aircraft/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("aircraft/form"))
                .andExpect(model().attributeExists("aircraft"));
    }

    @Test
    void testShowEditAircraftForm() throws Exception {
        // Given
        Aircraft aircraft = new Aircraft();
        aircraft.setRegistration("N789EF");
        aircraft.setType("Boeing 747");
        aircraft.setRange(8000);
        aircraft.setCapacity(400);
        Aircraft saved = aircraftRepository.save(aircraft);

        // When & Then
        mockMvc.perform(get("/web/aircraft/{id}/edit", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("aircraft/form"))
                .andExpect(model().attributeExists("aircraft"));
    }

    @Test
    void testShowAircraftDetails() throws Exception {
        // Given
        Aircraft aircraft = new Aircraft();
        aircraft.setRegistration("N101GH");
        aircraft.setType("Airbus A350");
        aircraft.setRange(7000);
        aircraft.setCapacity(300);
        Aircraft saved = aircraftRepository.save(aircraft);

        // When & Then
        mockMvc.perform(get("/web/aircraft/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("aircraft/details"))
                .andExpect(model().attributeExists("aircraft"));
    }

    @Test
    void testCreateAircraftSuccess() throws Exception {
        // When & Then
        mockMvc.perform(post("/web/aircraft")
                .param("registration", "N222IJ")
                .param("type", "Boeing 777")
                .param("range", "6000")
                .param("capacity", "350"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/aircraft"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify aircraft was created
        Aircraft fromDb = aircraftRepository.findByRegistration("N222IJ");
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getType()).isEqualTo("Boeing 777");
        assertThat(fromDb.getRange()).isEqualTo(6000);
        assertThat(fromDb.getCapacity()).isEqualTo(350);
    }

    @Test
    void testCreateAircraftValidationError() throws Exception {
        // When & Then - Missing required fields
        mockMvc.perform(post("/web/aircraft")
                .param("registration", "") // Empty registration
                .param("type", "") // Empty type
                .param("range", "-1") // Invalid range
                .param("capacity", "-1")) // Invalid capacity
                .andExpect(status().isOk())
                .andExpect(view().name("aircraft/form"))
                .andExpect(model().attributeExists("aircraft"))
                .andExpect(model().hasErrors());

        // Verify no aircraft was created
        assertThat(aircraftRepository.findAll()).isEmpty();
    }

    @Test
    void testUpdateAircraftSuccess() throws Exception {
        // Given
        Aircraft existingAircraft = new Aircraft();
        existingAircraft.setRegistration("N333KL");
        existingAircraft.setType("Airbus A380");
        existingAircraft.setRange(15000);
        existingAircraft.setCapacity(500);
        Aircraft saved = aircraftRepository.save(existingAircraft);

        // When & Then
        mockMvc.perform(post("/web/aircraft/{id}", saved.getId())
                .param("registration", "N333KL")
                .param("type", "Airbus A380-Updated")
                .param("range", "16000")
                .param("capacity", "550"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/aircraft"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify aircraft was updated
        Aircraft fromDb = aircraftRepository.findById(saved.getId()).orElse(null);
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getType()).isEqualTo("Airbus A380-Updated");
        assertThat(fromDb.getRange()).isEqualTo(16000);
        assertThat(fromDb.getCapacity()).isEqualTo(550);
    }

    @Test
    void testDeleteAircraftSuccess() throws Exception {
        // Given
        Aircraft aircraft = new Aircraft();
        aircraft.setRegistration("N444MN");
        aircraft.setType("Boeing 787");
        aircraft.setRange(5000);
        aircraft.setCapacity(250);
        Aircraft saved = aircraftRepository.save(aircraft);

        // When & Then
        mockMvc.perform(post("/web/aircraft/{id}/delete", saved.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/aircraft"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify aircraft was deleted
        assertThat(aircraftRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testShowAircraftNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/web/aircraft/{id}", 9999))
                .andExpect(status().isOk())
                .andExpect(view().name("aircraft/list"))
                .andExpect(model().attributeExists("aircraft"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void testEditAircraftNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/web/aircraft/{id}/edit", 9999))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/aircraft"));
    }

    @Test
    void testDeleteAircraftNotFound() throws Exception {
        // When & Then - Should still redirect with success (idempotent delete)
        mockMvc.perform(post("/web/aircraft/{id}/delete", 9999))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/aircraft"))
                .andExpect(flash().attributeExists("successMessage"));
    }
}
