package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.AirportDTO;
import com.antonio.skybase.entities.Airport;
import com.antonio.skybase.services.AirportService;
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

class AirportControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AirportService airportService;

    @InjectMocks
    private AirportController airportController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(airportController).build();
    }

    @Test
    void testCreateAirport() throws Exception {
        Airport airport = new Airport();
        airport.setId(1);
        airport.setName("Test Airport");
        airport.setCode("TST");

        when(airportService.create(any(AirportDTO.class))).thenReturn(airport);

        mockMvc.perform(post("/airports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test Airport\", \"code\": \"TST\", \"cityId\": 1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Airport"))
                .andExpect(jsonPath("$.code").value("TST"));
    }

    @Test
    void testCreateAirportWithBlankName() throws Exception {
        mockMvc.perform(post("/airports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\", \"code\": \"TST\", \"cityId\": 1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateAirportWithBlankCode() throws Exception {
        mockMvc.perform(post("/airports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test Airport\", \"code\": \"\", \"cityId\": 1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateAirportWithInvalidCodeSize() throws Exception {
        mockMvc.perform(post("/airports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test Airport\", \"code\": \"TS\", \"cityId\": 1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateAirportWithNullCityId() throws Exception {
        mockMvc.perform(post("/airports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test Airport\", \"code\": \"TST\", \"cityId\": null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllAirports() throws Exception {
        Airport airport1 = new Airport();
        airport1.setId(1);
        airport1.setName("Airport 1");
        airport1.setCode("A1");

        Airport airport2 = new Airport();
        airport2.setId(2);
        airport2.setName("Airport 2");
        airport2.setCode("A2");

        when(airportService.getAll()).thenReturn(List.of(airport1, airport2));

        mockMvc.perform(get("/airports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Airport 1"))
                .andExpect(jsonPath("$[0].code").value("A1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Airport 2"))
                .andExpect(jsonPath("$[1].code").value("A2"));
    }

    @Test
    void testGetAirportById() throws Exception {
        Airport airport = new Airport();
        airport.setId(1);
        airport.setName("Test Airport");
        airport.setCode("TST");

        when(airportService.getById(anyInt())).thenReturn(airport);

        mockMvc.perform(get("/airports/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Airport"))
                .andExpect(jsonPath("$.code").value("TST"));
    }

    @Test
    void testUpdateAirport() throws Exception {
        Airport airport = new Airport();
        airport.setId(1);
        airport.setName("Updated Airport");
        airport.setCode("UPD");

        when(airportService.update(anyInt(), any(AirportDTO.class))).thenReturn(airport);

        mockMvc.perform(put("/airports/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Updated Airport\", \"code\": \"UPD\", \"cityId\": 1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Airport"))
                .andExpect(jsonPath("$.code").value("UPD"));
    }

    @Test
    void testDeleteAirport() throws Exception {
        mockMvc.perform(delete("/airports/1"))
                .andExpect(status().isNoContent());
    }
}