package com.antonio.skybase.controllers;

import com.antonio.skybase.entities.Aircraft;
import com.antonio.skybase.services.AircraftService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AircraftControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AircraftService aircraftService;

    @InjectMocks
    private AircraftController aircraftController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(aircraftController).build();
    }

    @Test
    void testCreateAircraft() throws Exception {
        Aircraft aircraft = new Aircraft();
        aircraft.setId(1);
        aircraft.setRegistration("ABC123");
        aircraft.setType("Boeing 737");
        aircraft.setRange(5000);
        aircraft.setCapacity(200);

        when(aircraftService.create(any(Aircraft.class))).thenReturn(aircraft);

        mockMvc.perform(post("/aircraft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"registration\": \"ABC123\", \"type\": \"Boeing 737\", \"range\": 5000, \"capacity\": 200}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.registration").value("ABC123"))
                .andExpect(jsonPath("$.type").value("Boeing 737"))
                .andExpect(jsonPath("$.range").value(5000))
                .andExpect(jsonPath("$.capacity").value(200));
    }

    @Test
    void testCreateAircraftWithBlankRegistration() throws Exception {
        mockMvc.perform(post("/aircraft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"registration\": \"\", \"type\": \"Boeing 737\", \"range\": 5000, \"capacity\": 200}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateAircraftWithBlankType() throws Exception {
        mockMvc.perform(post("/aircraft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"registration\": \"ABC123\", \"type\": \"\", \"range\": 5000, \"capacity\": 200}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateAircraftWithNegativeRange() throws Exception {
        mockMvc.perform(post("/aircraft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"registration\": \"ABC123\", \"type\": \"Boeing 737\", \"range\": -5000, \"capacity\": 200}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateAircraftWithNegativeCapacity() throws Exception {
        mockMvc.perform(post("/aircraft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"registration\": \"ABC123\", \"type\": \"Boeing 737\", \"range\": 5000, \"capacity\": -200}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllAircraft() throws Exception {
        Aircraft aircraft1 = new Aircraft();
        aircraft1.setId(1);
        aircraft1.setRegistration("ABC123");
        aircraft1.setType("Boeing 737");
        aircraft1.setRange(5000);
        aircraft1.setCapacity(200);

        Aircraft aircraft2 = new Aircraft();
        aircraft2.setId(2);
        aircraft2.setRegistration("DEF456");
        aircraft2.setType("Airbus A320");
        aircraft2.setRange(4000);
        aircraft2.setCapacity(180);

        when(aircraftService.getAll()).thenReturn(List.of(aircraft1, aircraft2));

        mockMvc.perform(get("/aircraft"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].registration").value("ABC123"))
                .andExpect(jsonPath("$[0].type").value("Boeing 737"))
                .andExpect(jsonPath("$[0].range").value(5000))
                .andExpect(jsonPath("$[0].capacity").value(200))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].registration").value("DEF456"))
                .andExpect(jsonPath("$[1].type").value("Airbus A320"))
                .andExpect(jsonPath("$[1].range").value(4000))
                .andExpect(jsonPath("$[1].capacity").value(180));
    }

    @Test
    void testGetAircraftById() throws Exception {
        Aircraft aircraft = new Aircraft();
        aircraft.setId(1);
        aircraft.setRegistration("ABC123");
        aircraft.setType("Boeing 737");
        aircraft.setRange(5000);
        aircraft.setCapacity(200);

        when(aircraftService.getById(anyInt())).thenReturn(aircraft);

        mockMvc.perform(get("/aircraft/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.registration").value("ABC123"))
                .andExpect(jsonPath("$.type").value("Boeing 737"))
                .andExpect(jsonPath("$.range").value(5000))
                .andExpect(jsonPath("$.capacity").value(200));
    }

    @Test
    void testUpdateAircraft() throws Exception {
        Aircraft aircraft = new Aircraft();
        aircraft.setId(1);
        aircraft.setRegistration("ABC123");
        aircraft.setType("Boeing 737");
        aircraft.setRange(5000);
        aircraft.setCapacity(200);

        when(aircraftService.update(anyInt(), any(Aircraft.class))).thenReturn(aircraft);

        mockMvc.perform(put("/aircraft/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"registration\": \"ABC123\", \"type\": \"Boeing 737\", \"range\": 5000, \"capacity\": 200}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.registration").value("ABC123"))
                .andExpect(jsonPath("$.type").value("Boeing 737"))
                .andExpect(jsonPath("$.range").value(5000))
                .andExpect(jsonPath("$.capacity").value(200));
    }

    @Test
    void testDeleteAircraft() throws Exception {
        mockMvc.perform(delete("/aircraft/1"))
                .andExpect(status().isNoContent());
    }
}