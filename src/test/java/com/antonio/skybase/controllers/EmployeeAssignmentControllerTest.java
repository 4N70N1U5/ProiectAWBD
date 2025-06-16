package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.EmployeeAssignmentDTO;
import com.antonio.skybase.entities.EmployeeAssignment;
import com.antonio.skybase.entities.EmployeeAssignmentId;
import com.antonio.skybase.services.EmployeeAssignmentService;
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

class EmployeeAssignmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeeAssignmentService employeeAssignmentService;

    @InjectMocks
    private EmployeeAssignmentController employeeAssignmentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(employeeAssignmentController).build();
    }

    @Test
    void testCreateEmployeeAssignment() throws Exception {
        EmployeeAssignmentId id = new EmployeeAssignmentId();
        id.setEmployeeId(1);
        id.setFlightId(1);
        id.setDate(LocalDate.of(2023, 10, 10));

        EmployeeAssignment employeeAssignment = new EmployeeAssignment();
        employeeAssignment.setId(id);

        when(employeeAssignmentService.create(any(EmployeeAssignmentDTO.class))).thenReturn(employeeAssignment);

        mockMvc.perform(post("/employee-assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeeId\": 1, \"flightId\": 1, \"date\": \"2023-10-10\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id.employeeId").value(1))
                .andExpect(jsonPath("$.id.flightId").value(1));
    }

    @Test
    void testCreateEmployeeAssignmentWithNullEmployeeId() throws Exception {
        mockMvc.perform(post("/employee-assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeeId\": null, \"flightId\": 1, \"date\": \"2023-10-10\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateEmployeeAssignmentWithNullFlightId() throws Exception {
        mockMvc.perform(post("/employee-assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeeId\": 1, \"flightId\": null, \"date\": \"2023-10-10\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateEmployeeAssignmentWithNullDate() throws Exception {
        mockMvc.perform(post("/employee-assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeeId\": 1, \"flightId\": 1, \"date\": null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllEmployeeAssignments() throws Exception {
        EmployeeAssignmentId id1 = new EmployeeAssignmentId();
        id1.setEmployeeId(1);
        id1.setFlightId(1);
        id1.setDate(LocalDate.of(2023, 10, 10));

        EmployeeAssignmentId id2 = new EmployeeAssignmentId();
        id2.setEmployeeId(2);
        id2.setFlightId(2);
        id2.setDate(LocalDate.of(2023, 10, 11));

        EmployeeAssignment employeeAssignment1 = new EmployeeAssignment();
        employeeAssignment1.setId(id1);

        EmployeeAssignment employeeAssignment2 = new EmployeeAssignment();
        employeeAssignment2.setId(id2);

        when(employeeAssignmentService.getAll()).thenReturn(List.of(employeeAssignment1, employeeAssignment2));

        mockMvc.perform(get("/employee-assignments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id.employeeId").value(1))
                .andExpect(jsonPath("$[0].id.flightId").value(1))
                .andExpect(jsonPath("$[1].id.employeeId").value(2))
                .andExpect(jsonPath("$[1].id.flightId").value(2));
    }

    @Test
    void testGetEmployeeAssignmentById() throws Exception {
        EmployeeAssignmentId id = new EmployeeAssignmentId();
        id.setEmployeeId(1);
        id.setFlightId(1);
        id.setDate(LocalDate.of(2023, 10, 10));

        EmployeeAssignment employeeAssignment = new EmployeeAssignment();
        employeeAssignment.setId(id);

        when(employeeAssignmentService.getById(any(EmployeeAssignmentId.class))).thenReturn(employeeAssignment);

        mockMvc.perform(get("/employee-assignments/1/1/2023-10-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id.employeeId").value(1))
                .andExpect(jsonPath("$.id.flightId").value(1));
    }

    @Test
    void testUpdateEmployeeAssignment() throws Exception {
        EmployeeAssignmentId id = new EmployeeAssignmentId();
        id.setEmployeeId(1);
        id.setFlightId(1);
        id.setDate(LocalDate.of(2023, 10, 10));

        EmployeeAssignment employeeAssignment = new EmployeeAssignment();
        employeeAssignment.setId(id);

        when(employeeAssignmentService.update(any(EmployeeAssignmentId.class), any(EmployeeAssignmentDTO.class))).thenReturn(employeeAssignment);

        mockMvc.perform(put("/employee-assignments/1/1/2023-10-10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeeId\": 1, \"flightId\": 1, \"date\": \"2023-10-10\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id.employeeId").value(1))
                .andExpect(jsonPath("$.id.flightId").value(1));
    }

    @Test
    void testDeleteEmployeeAssignment() throws Exception {
        mockMvc.perform(delete("/employee-assignments/1/1/2023-10-10"))
                .andExpect(status().isNoContent());
    }
}