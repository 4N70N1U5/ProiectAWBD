package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.AircraftAssignmentDTO;
import com.antonio.skybase.entities.AircraftAssignment;
import com.antonio.skybase.entities.AircraftAssignmentId;
import com.antonio.skybase.services.AircraftAssignmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AircraftAssignmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AircraftAssignmentService aircraftAssignmentService;

    @InjectMocks
    private AircraftAssignmentController aircraftAssignmentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(aircraftAssignmentController).build();
    }

    @Test
    void testCreateAircraftAssignment() throws Exception {
        AircraftAssignmentId id = new AircraftAssignmentId();
        id.setAircraftId(1);
        id.setFlightId(1);
        id.setDate(LocalDate.of(2023, 10, 10));

        AircraftAssignment aircraftAssignment = new AircraftAssignment();
        aircraftAssignment.setId(id);

        when(aircraftAssignmentService.create(any(AircraftAssignmentDTO.class))).thenReturn(aircraftAssignment);

        mockMvc.perform(post("/aircraft-assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"aircraftId\": 1, \"flightId\": 1, \"date\": \"2023-10-10\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id.aircraftId").value(1))
                .andExpect(jsonPath("$.id.flightId").value(1));
    }

    @Test
    void testCreateAircraftAssignmentWithNullAircraftId() throws Exception {
        mockMvc.perform(post("/aircraft-assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"aircraftId\": null, \"flightId\": 1, \"date\": \"2023-10-10\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateAircraftAssignmentWithNullFlightId() throws Exception {
        mockMvc.perform(post("/aircraft-assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"aircraftId\": 1, \"flightId\": null, \"date\": \"2023-10-10\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateAircraftAssignmentWithNullDate() throws Exception {
        mockMvc.perform(post("/aircraft-assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"aircraftId\": 1, \"flightId\": 1, \"date\": null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllAircraftAssignments() throws Exception {
        AircraftAssignmentId id1 = new AircraftAssignmentId();
        id1.setAircraftId(1);
        id1.setFlightId(1);
        id1.setDate(LocalDate.of(2023, 10, 10));

        AircraftAssignmentId id2 = new AircraftAssignmentId();
        id2.setAircraftId(2);
        id2.setFlightId(2);
        id2.setDate(LocalDate.of(2023, 10, 11));

        AircraftAssignment aircraftAssignment1 = new AircraftAssignment();
        aircraftAssignment1.setId(id1);

        AircraftAssignment aircraftAssignment2 = new AircraftAssignment();
        aircraftAssignment2.setId(id2);

        when(aircraftAssignmentService.getAll()).thenReturn(List.of(aircraftAssignment1, aircraftAssignment2));

        mockMvc.perform(get("/aircraft-assignments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id.aircraftId").value(1))
                .andExpect(jsonPath("$[0].id.flightId").value(1))
                .andExpect(jsonPath("$[1].id.aircraftId").value(2))
                .andExpect(jsonPath("$[1].id.flightId").value(2));
    }

    @Test
    void testGetAircraftAssignmentById() throws Exception {
        AircraftAssignmentId id = new AircraftAssignmentId();
        id.setAircraftId(1);
        id.setFlightId(1);
        id.setDate(LocalDate.of(2023, 10, 10));

        AircraftAssignment aircraftAssignment = new AircraftAssignment();
        aircraftAssignment.setId(id);

        when(aircraftAssignmentService.getById(any(AircraftAssignmentId.class))).thenReturn(aircraftAssignment);

        mockMvc.perform(get("/aircraft-assignments/1/1/2023-10-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id.aircraftId").value(1))
                .andExpect(jsonPath("$.id.flightId").value(1));
    }

    @Test
    void testUpdateAircraftAssignment() throws Exception {
        AircraftAssignmentId id = new AircraftAssignmentId();
        id.setAircraftId(1);
        id.setFlightId(1);
        id.setDate(LocalDate.of(2023, 10, 10));

        AircraftAssignment aircraftAssignment = new AircraftAssignment();
        aircraftAssignment.setId(id);

        when(aircraftAssignmentService.update(any(AircraftAssignmentId.class), any(AircraftAssignmentDTO.class))).thenReturn(aircraftAssignment);

        mockMvc.perform(put("/aircraft-assignments/1/1/2023-10-10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"aircraftId\": 1, \"flightId\": 1, \"date\": \"2023-10-10\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id.aircraftId").value(1))
                .andExpect(jsonPath("$.id.flightId").value(1));
    }

    @Test
    void testDeleteAircraftAssignment() throws Exception {
        mockMvc.perform(delete("/aircraft-assignments/1/1/2023-10-10"))
                .andExpect(status().isNoContent());
    }
}