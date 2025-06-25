package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.AircraftAssignmentDTO;
import com.antonio.skybase.entities.*;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.services.AircraftAssignmentService;
import com.antonio.skybase.services.AircraftService;
import com.antonio.skybase.services.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AircraftAssignmentMvcControllerTest {

    @Mock
    private AircraftAssignmentService aircraftAssignmentService;

    @Mock
    private AircraftService aircraftService;

    @Mock
    private FlightService flightService;

    @InjectMocks
    private AircraftAssignmentMvcController aircraftAssignmentMvcController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(aircraftAssignmentMvcController).build();
    }

    @Test
    void getAllAircraftAssignments_ShouldReturnListView() throws Exception {
        // Arrange
        AircraftAssignment assignment1 = new AircraftAssignment();
        AircraftAssignmentId id1 = new AircraftAssignmentId();
        id1.setAircraftId(1);
        id1.setFlightId(1);
        id1.setDate(LocalDate.of(2024, 1, 1));
        assignment1.setId(id1);

        List<AircraftAssignment> assignments = Arrays.asList(assignment1);
        when(aircraftAssignmentService.getAll()).thenReturn(assignments);

        // Act & Assert
        mockMvc.perform(get("/web/aircraft-assignments"))
                .andExpect(status().isOk())
                .andExpect(view().name("aircraft-assignments/list"))
                .andExpect(model().attributeExists("assignments"))
                .andExpect(model().attribute("assignments", assignments));

        verify(aircraftAssignmentService).getAll();
    }

    @Test
    void showCreateForm_ShouldReturnFormView() throws Exception {
        // Arrange
        Aircraft aircraft = new Aircraft();
        aircraft.setId(1);
        aircraft.setType("Boeing 737");

        Flight flight = new Flight();
        flight.setId(1);
        flight.setNumber("AA123");

        List<Aircraft> aircraftList = Arrays.asList(aircraft);
        List<Flight> flights = Arrays.asList(flight);
        when(aircraftService.getAll()).thenReturn(aircraftList);
        when(flightService.getAll()).thenReturn(flights);

        // Act & Assert
        mockMvc.perform(get("/web/aircraft-assignments/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("aircraft-assignments/form"))
                .andExpect(model().attributeExists("aircraftAssignmentDTO"))
                .andExpect(model().attributeExists("aircraft"))
                .andExpect(model().attributeExists("flights"));

        verify(aircraftService).getAll();
        verify(flightService).getAll();
    }

    @Test
    void createAircraftAssignment_WithValidData_ShouldRedirectToList() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/web/aircraft-assignments")
                .param("aircraftId", "1")
                .param("flightId", "1")
                .param("date", "2024-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/aircraft-assignments"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(aircraftAssignmentService).create(any(AircraftAssignmentDTO.class));
    }

    @Test
    void createAircraftAssignment_WithInvalidData_ShouldReturnFormView() throws Exception {
        // Arrange
        Aircraft aircraft = new Aircraft();
        aircraft.setId(1);
        aircraft.setType("Boeing 737");

        Flight flight = new Flight();
        flight.setId(1);
        flight.setNumber("AA123");

        List<Aircraft> aircraftList = Arrays.asList(aircraft);
        List<Flight> flights = Arrays.asList(flight);
        when(aircraftService.getAll()).thenReturn(aircraftList);
        when(flightService.getAll()).thenReturn(flights);

        // Act & Assert
        mockMvc.perform(post("/web/aircraft-assignments")
                .param("aircraftId", "1")
                .param("flightId", "") // Invalid flight ID
                .param("date", "2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("aircraft-assignments/form"))
                .andExpect(model().attributeExists("aircraftAssignmentDTO"))
                .andExpect(model().attributeExists("aircraft"))
                .andExpect(model().attributeExists("flights"))
                .andExpect(model().hasErrors());

        verify(aircraftService).getAll();
        verify(flightService).getAll();
        verifyNoInteractions(aircraftAssignmentService);
    }

    @Test
    void createAircraftAssignment_ServiceThrowsException_ShouldReturnFormWithErrorMessage() throws Exception {
        // Arrange
        Aircraft aircraft = new Aircraft();
        aircraft.setId(1);
        aircraft.setType("Boeing 737");

        Flight flight = new Flight();
        flight.setId(1);
        flight.setNumber("AA123");

        List<Aircraft> aircraftList = Arrays.asList(aircraft);
        List<Flight> flights = Arrays.asList(flight);
        when(aircraftService.getAll()).thenReturn(aircraftList);
        when(flightService.getAll()).thenReturn(flights);
        doThrow(new RuntimeException("Database error")).when(aircraftAssignmentService)
                .create(any(AircraftAssignmentDTO.class));

        // Act & Assert
        mockMvc.perform(post("/web/aircraft-assignments")
                .param("aircraftId", "1")
                .param("flightId", "1")
                .param("date", "2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("aircraft-assignments/form"))
                .andExpect(model().attributeExists("aircraftAssignmentDTO"))
                .andExpect(model().attributeExists("aircraft"))
                .andExpect(model().attributeExists("flights"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(aircraftAssignmentService).create(any(AircraftAssignmentDTO.class));
        verify(aircraftService).getAll();
        verify(flightService).getAll();
    }

    @Test
    void getAircraftAssignmentById_WithValidId_ShouldReturnDetailsView() throws Exception {
        // Arrange
        AircraftAssignmentId id = new AircraftAssignmentId();
        id.setAircraftId(1);
        id.setFlightId(1);
        id.setDate(LocalDate.of(2024, 1, 1));

        AircraftAssignment assignment = new AircraftAssignment();
        assignment.setId(id);
        when(aircraftAssignmentService.getById(any(AircraftAssignmentId.class))).thenReturn(assignment);

        // Act & Assert
        mockMvc.perform(get("/web/aircraft-assignments/1/1/2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("aircraft-assignments/details"))
                .andExpect(model().attributeExists("assignment"))
                .andExpect(model().attribute("assignment", assignment));

        verify(aircraftAssignmentService).getById(any(AircraftAssignmentId.class));
    }

    @Test
    void getAircraftAssignmentById_WithInvalidId_ShouldRedirectToList() throws Exception {
        // Arrange
        when(aircraftAssignmentService.getById(any(AircraftAssignmentId.class)))
                .thenThrow(new NotFoundException("Aircraft assignment not found"));

        // Act & Assert
        mockMvc.perform(get("/web/aircraft-assignments/999/999/2024-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/aircraft-assignments"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(aircraftAssignmentService).getById(any(AircraftAssignmentId.class));
    }

    @Test
    void showEditForm_WithValidId_ShouldReturnFormView() throws Exception {
        // Arrange
        AircraftAssignmentId id = new AircraftAssignmentId();
        id.setAircraftId(1);
        id.setFlightId(1);
        id.setDate(LocalDate.of(2024, 1, 1));

        AircraftAssignment assignment = new AircraftAssignment();
        assignment.setId(id);

        Aircraft aircraft = new Aircraft();
        aircraft.setId(1);
        aircraft.setType("Boeing 737");

        Flight flight = new Flight();
        flight.setId(1);
        flight.setNumber("AA123");

        List<Aircraft> aircraftList = Arrays.asList(aircraft);
        List<Flight> flights = Arrays.asList(flight);

        when(aircraftAssignmentService.getById(any(AircraftAssignmentId.class))).thenReturn(assignment);
        when(aircraftService.getAll()).thenReturn(aircraftList);
        when(flightService.getAll()).thenReturn(flights);

        // Act & Assert
        mockMvc.perform(get("/web/aircraft-assignments/1/1/2024-01-01/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("aircraft-assignments/form"))
                .andExpect(model().attributeExists("aircraftAssignmentDTO"))
                .andExpect(model().attributeExists("originalId"))
                .andExpect(model().attributeExists("aircraft"))
                .andExpect(model().attributeExists("flights"));

        verify(aircraftAssignmentService).getById(any(AircraftAssignmentId.class));
        verify(aircraftService).getAll();
        verify(flightService).getAll();
    }

    @Test
    void showEditForm_WithInvalidId_ShouldRedirectToList() throws Exception {
        // Arrange
        when(aircraftAssignmentService.getById(any(AircraftAssignmentId.class)))
                .thenThrow(new NotFoundException("Aircraft assignment not found"));

        // Act & Assert
        mockMvc.perform(get("/web/aircraft-assignments/999/999/2024-01-01/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/aircraft-assignments"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(aircraftAssignmentService).getById(any(AircraftAssignmentId.class));
        verifyNoInteractions(aircraftService);
        verifyNoInteractions(flightService);
    }

    @Test
    void updateAircraftAssignment_WithValidData_ShouldRedirectToList() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/web/aircraft-assignments/1/1/2024-01-01")
                .param("aircraftId", "1")
                .param("flightId", "1")
                .param("date", "2024-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/aircraft-assignments"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(aircraftAssignmentService).update(any(AircraftAssignmentId.class), any(AircraftAssignmentDTO.class));
    }

    @Test
    void updateAircraftAssignment_WithInvalidData_ShouldReturnFormView() throws Exception {
        // Arrange
        Aircraft aircraft = new Aircraft();
        aircraft.setId(1);
        aircraft.setType("Boeing 737");

        Flight flight = new Flight();
        flight.setId(1);
        flight.setNumber("AA123");

        List<Aircraft> aircraftList = Arrays.asList(aircraft);
        List<Flight> flights = Arrays.asList(flight);
        when(aircraftService.getAll()).thenReturn(aircraftList);
        when(flightService.getAll()).thenReturn(flights);

        // Act & Assert
        mockMvc.perform(post("/web/aircraft-assignments/1/1/2024-01-01")
                .param("aircraftId", "") // Invalid aircraft ID
                .param("flightId", "1")
                .param("date", "2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("aircraft-assignments/form"))
                .andExpect(model().attributeExists("aircraftAssignmentDTO"))
                .andExpect(model().attributeExists("originalId"))
                .andExpect(model().attributeExists("aircraft"))
                .andExpect(model().attributeExists("flights"))
                .andExpect(model().hasErrors());

        verify(aircraftService).getAll();
        verify(flightService).getAll();
        verifyNoInteractions(aircraftAssignmentService);
    }

    @Test
    void updateAircraftAssignment_ServiceThrowsException_ShouldReturnFormWithErrorMessage() throws Exception {
        // Arrange
        Aircraft aircraft = new Aircraft();
        aircraft.setId(1);
        aircraft.setType("Boeing 737");

        Flight flight = new Flight();
        flight.setId(1);
        flight.setNumber("AA123");

        List<Aircraft> aircraftList = Arrays.asList(aircraft);
        List<Flight> flights = Arrays.asList(flight);
        when(aircraftService.getAll()).thenReturn(aircraftList);
        when(flightService.getAll()).thenReturn(flights);
        doThrow(new RuntimeException("Update failed")).when(aircraftAssignmentService)
                .update(any(AircraftAssignmentId.class), any(AircraftAssignmentDTO.class));

        // Act & Assert
        mockMvc.perform(post("/web/aircraft-assignments/1/1/2024-01-01")
                .param("aircraftId", "1")
                .param("flightId", "1")
                .param("date", "2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("aircraft-assignments/form"))
                .andExpect(model().attributeExists("aircraftAssignmentDTO"))
                .andExpect(model().attributeExists("originalId"))
                .andExpect(model().attributeExists("aircraft"))
                .andExpect(model().attributeExists("flights"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(aircraftAssignmentService).update(any(AircraftAssignmentId.class), any(AircraftAssignmentDTO.class));
        verify(aircraftService).getAll();
        verify(flightService).getAll();
    }

    @Test
    void deleteAircraftAssignment_WithValidId_ShouldRedirectToList() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/web/aircraft-assignments/1/1/2024-01-01/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/aircraft-assignments"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(aircraftAssignmentService).delete(any(AircraftAssignmentId.class));
    }

    @Test
    void deleteAircraftAssignment_ServiceThrowsException_ShouldRedirectWithErrorMessage() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Delete failed")).when(aircraftAssignmentService)
                .delete(any(AircraftAssignmentId.class));

        // Act & Assert
        mockMvc.perform(post("/web/aircraft-assignments/1/1/2024-01-01/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/aircraft-assignments"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(aircraftAssignmentService).delete(any(AircraftAssignmentId.class));
    }
}
