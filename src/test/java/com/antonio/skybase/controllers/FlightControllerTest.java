package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.FlightDTO;
import com.antonio.skybase.entities.Flight;
import com.antonio.skybase.services.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FlightControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FlightService flightService;

    @InjectMocks
    private FlightController flightController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(flightController).build();
    }

    @Test
    void testCreateFlight() throws Exception {
        Flight flight = new Flight();
        flight.setId(1);
        flight.setNumber("FL123");
        flight.setDepartureTime(LocalTime.of(10, 0));
        flight.setArrivalTime(LocalTime.of(12, 0));
        flight.setDistance(500);

        when(flightService.create(any(FlightDTO.class))).thenReturn(flight);

        mockMvc.perform(post("/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"number\": \"FL123\", \"departureAirportId\": 1, \"arrivalAirportId\": 2, \"departureTime\": \"10:00:00\", \"arrivalTime\": \"12:00:00\", \"distance\": 500}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.number").value("FL123"))
                .andExpect(jsonPath("$.distance").value(500));
    }

    @Test
    void testCreateFlightWithBlankNumber() throws Exception {
        mockMvc.perform(post("/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"number\": \"\", \"departureAirportId\": 1, \"arrivalAirportId\": 2, \"departureTime\": \"10:00:00\", \"arrivalTime\": \"12:00:00\", \"distance\": 500}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateFlightWithNegativeDistance() throws Exception {
        mockMvc.perform(post("/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"number\": \"FL123\", \"departureAirportId\": 1, \"arrivalAirportId\": 2, \"departureTime\": \"10:00:00\", \"arrivalTime\": \"12:00:00\", \"distance\": -500}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllFlights() throws Exception {
        Flight flight1 = new Flight();
        flight1.setId(1);
        flight1.setNumber("FL123");
        flight1.setDepartureTime(LocalTime.of(10, 0));
        flight1.setArrivalTime(LocalTime.of(12, 0));
        flight1.setDistance(500);

        Flight flight2 = new Flight();
        flight2.setId(2);
        flight2.setNumber("FL124");
        flight2.setDepartureTime(LocalTime.of(14, 0));
        flight2.setArrivalTime(LocalTime.of(16, 0));
        flight2.setDistance(600);

        when(flightService.getAll()).thenReturn(List.of(flight1, flight2));

        mockMvc.perform(get("/flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].number").value("FL123"))
                .andExpect(jsonPath("$[0].distance").value(500))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].number").value("FL124"))
                .andExpect(jsonPath("$[1].distance").value(600));
    }

    @Test
    void testGetFlightById() throws Exception {
        Flight flight = new Flight();
        flight.setId(1);
        flight.setNumber("FL123");
        flight.setDepartureTime(LocalTime.of(10, 0));
        flight.setArrivalTime(LocalTime.of(12, 0));
        flight.setDistance(500);

        when(flightService.getById(anyInt())).thenReturn(flight);

        mockMvc.perform(get("/flights/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.number").value("FL123"))
                .andExpect(jsonPath("$.distance").value(500));
    }

    @Test
    void testUpdateFlight() throws Exception {
        Flight flight = new Flight();
        flight.setId(1);
        flight.setNumber("FL123");
        flight.setDepartureTime(LocalTime.of(10, 0));
        flight.setArrivalTime(LocalTime.of(12, 0));
        flight.setDistance(500);

        when(flightService.update(anyInt(), any(FlightDTO.class))).thenReturn(flight);

        mockMvc.perform(put("/flights/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"number\": \"FL123\", \"departureAirportId\": 1, \"arrivalAirportId\": 2, \"departureTime\": \"10:00:00\", \"arrivalTime\": \"12:00:00\", \"distance\": 500}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.number").value("FL123"))
                .andExpect(jsonPath("$.distance").value(500));
    }

    @Test
    void testDeleteFlight() throws Exception {
        mockMvc.perform(delete("/flights/1"))
                .andExpect(status().isNoContent());
    }
}