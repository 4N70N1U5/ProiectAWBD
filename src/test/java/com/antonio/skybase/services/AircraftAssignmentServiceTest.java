package com.antonio.skybase.services;

import com.antonio.skybase.dtos.AircraftAssignmentDTO;
import com.antonio.skybase.entities.Aircraft;
import com.antonio.skybase.entities.AircraftAssignment;
import com.antonio.skybase.entities.AircraftAssignmentId;
import com.antonio.skybase.entities.Flight;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.repositories.AircraftAssignmentRepository;
import com.antonio.skybase.repositories.AircraftRepository;
import com.antonio.skybase.repositories.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class AircraftAssignmentServiceTest {

    @Mock
    private AircraftAssignmentRepository aircraftAssignmentRepository;

    @Mock
    private AircraftRepository aircraftRepository;

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private AircraftAssignmentService aircraftAssignmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAircraftAssignment() {
        AircraftAssignmentDTO aircraftAssignmentDTO = new AircraftAssignmentDTO();
        aircraftAssignmentDTO.setAircraftId(1);
        aircraftAssignmentDTO.setFlightId(1);
        aircraftAssignmentDTO.setDate(LocalDate.of(2023, 10, 10));

        Aircraft aircraft = new Aircraft();
        aircraft.setId(1);
        aircraft.setRange(1000);

        Flight flight = new Flight();
        flight.setId(1);
        flight.setDistance(500);

        AircraftAssignmentId id = new AircraftAssignmentId();
        id.setAircraftId(1);
        id.setFlightId(1);
        id.setDate(LocalDate.of(2023, 10, 10));

        AircraftAssignment aircraftAssignment = new AircraftAssignment();
        aircraftAssignment.setId(id);
        aircraftAssignment.setAircraft(aircraft);
        aircraftAssignment.setFlight(flight);

        when(aircraftRepository.findById(anyInt())).thenReturn(Optional.of(aircraft));
        when(flightRepository.findById(anyInt())).thenReturn(Optional.of(flight));
        when(aircraftAssignmentRepository.save(any(AircraftAssignment.class))).thenReturn(aircraftAssignment);

        AircraftAssignment createdAircraftAssignment = aircraftAssignmentService.create(aircraftAssignmentDTO);

        assertNotNull(createdAircraftAssignment);
        assertEquals(1, createdAircraftAssignment.getId().getAircraftId());
        assertEquals(1, createdAircraftAssignment.getId().getFlightId());
        assertEquals(LocalDate.of(2023, 10, 10), createdAircraftAssignment.getId().getDate());
    }

    @Test
    void testCreateAircraftAssignmentWithInvalidAircraft() {
        AircraftAssignmentDTO aircraftAssignmentDTO = new AircraftAssignmentDTO();
        aircraftAssignmentDTO.setAircraftId(1);
        aircraftAssignmentDTO.setFlightId(1);
        aircraftAssignmentDTO.setDate(LocalDate.of(2023, 10, 10));

        when(aircraftRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> aircraftAssignmentService.create(aircraftAssignmentDTO));
    }

    @Test
    void testGetAllAircraftAssignments() {
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

        List<AircraftAssignment> aircraftAssignments = Arrays.asList(aircraftAssignment1, aircraftAssignment2);

        when(aircraftAssignmentRepository.findAll()).thenReturn(aircraftAssignments);

        List<AircraftAssignment> result = aircraftAssignmentService.getAll();

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId().getAircraftId());
        assertEquals(2, result.get(1).getId().getAircraftId());
    }

    @Test
    void testGetAircraftAssignmentById() {
        AircraftAssignmentId id = new AircraftAssignmentId();
        id.setAircraftId(1);
        id.setFlightId(1);
        id.setDate(LocalDate.of(2023, 10, 10));

        AircraftAssignment aircraftAssignment = new AircraftAssignment();
        aircraftAssignment.setId(id);

        when(aircraftAssignmentRepository.findById(any(AircraftAssignmentId.class))).thenReturn(Optional.of(aircraftAssignment));

        AircraftAssignment result = aircraftAssignmentService.getById(id);

        assertNotNull(result);
        assertEquals(1, result.getId().getAircraftId());
        assertEquals(1, result.getId().getFlightId());
        assertEquals(LocalDate.of(2023, 10, 10), result.getId().getDate());
    }

    @Test
    void testGetAircraftAssignmentByIdNotFound() {
        AircraftAssignmentId id = new AircraftAssignmentId();
        id.setAircraftId(1);
        id.setFlightId(1);
        id.setDate(LocalDate.of(2023, 10, 10));

        when(aircraftAssignmentRepository.findById(any(AircraftAssignmentId.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> aircraftAssignmentService.getById(id));
    }

    @Test
    void testUpdateAircraftAssignment() {
        AircraftAssignmentId id = new AircraftAssignmentId();
        id.setAircraftId(1);
        id.setFlightId(1);
        id.setDate(LocalDate.of(2023, 10, 10));

        AircraftAssignmentDTO aircraftAssignmentDTO = new AircraftAssignmentDTO();
        aircraftAssignmentDTO.setAircraftId(1);
        aircraftAssignmentDTO.setFlightId(1);
        aircraftAssignmentDTO.setDate(LocalDate.of(2023, 10, 10));

        Aircraft aircraft = new Aircraft();
        aircraft.setId(1);
        aircraft.setRange(1000);

        Flight flight = new Flight();
        flight.setId(1);
        flight.setDistance(500);

        AircraftAssignment aircraftAssignment = new AircraftAssignment();
        aircraftAssignment.setId(id);
        aircraftAssignment.setAircraft(aircraft);
        aircraftAssignment.setFlight(flight);

        when(aircraftAssignmentRepository.findById(any(AircraftAssignmentId.class))).thenReturn(Optional.of(aircraftAssignment));
        when(aircraftRepository.findById(anyInt())).thenReturn(Optional.of(aircraft));
        when(flightRepository.findById(anyInt())).thenReturn(Optional.of(flight));
        when(aircraftAssignmentRepository.save(any(AircraftAssignment.class))).thenReturn(aircraftAssignment);

        AircraftAssignment updatedAircraftAssignment = aircraftAssignmentService.update(id, aircraftAssignmentDTO);

        assertNotNull(updatedAircraftAssignment);
        assertEquals(1, updatedAircraftAssignment.getId().getAircraftId());
        assertEquals(1, updatedAircraftAssignment.getId().getFlightId());
        assertEquals(LocalDate.of(2023, 10, 10), updatedAircraftAssignment.getId().getDate());
    }

    @Test
    void testUpdateAircraftAssignmentWithInvalidAircraft() {
        AircraftAssignmentId id = new AircraftAssignmentId();
        id.setAircraftId(1);
        id.setFlightId(1);
        id.setDate(LocalDate.of(2023, 10, 10));

        AircraftAssignmentDTO aircraftAssignmentDTO = new AircraftAssignmentDTO();
        aircraftAssignmentDTO.setAircraftId(1);
        aircraftAssignmentDTO.setFlightId(1);
        aircraftAssignmentDTO.setDate(LocalDate.of(2023, 10, 10));

        when(aircraftAssignmentRepository.findById(any(AircraftAssignmentId.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> aircraftAssignmentService.update(id, aircraftAssignmentDTO));
    }

    @Test
    void testDeleteAircraftAssignment() {
        AircraftAssignmentId id = new AircraftAssignmentId();
        id.setAircraftId(1);
        id.setFlightId(1);
        id.setDate(LocalDate.of(2023, 10, 10));

        doNothing().when(aircraftAssignmentRepository).deleteById(any(AircraftAssignmentId.class));

        aircraftAssignmentService.delete(id);

        verify(aircraftAssignmentRepository, times(1)).deleteById(id);
    }
}