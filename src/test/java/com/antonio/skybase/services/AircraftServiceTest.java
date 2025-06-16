package com.antonio.skybase.services;

import com.antonio.skybase.entities.Aircraft;
import com.antonio.skybase.exceptions.BadRequestException;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.repositories.AircraftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AircraftServiceTest {

    @Mock
    private AircraftRepository aircraftRepository;

    @InjectMocks
    private AircraftService aircraftService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAircraft() {
        Aircraft aircraft = new Aircraft();
        aircraft.setRegistration("ABC123");
        aircraft.setType("Boeing 737");
        aircraft.setRange(5000);
        aircraft.setCapacity(200);

        when(aircraftRepository.existsByRegistration(anyString())).thenReturn(false);
        when(aircraftRepository.save(any(Aircraft.class))).thenReturn(aircraft);

        Aircraft createdAircraft = aircraftService.create(aircraft);

        assertNotNull(createdAircraft);
        assertEquals("ABC123", createdAircraft.getRegistration());
        assertEquals("Boeing 737", createdAircraft.getType());
        assertEquals(5000, createdAircraft.getRange());
        assertEquals(200, createdAircraft.getCapacity());
    }

    @Test
    void testCreateAircraftWithExistingRegistration() {
        Aircraft aircraft = new Aircraft();
        aircraft.setRegistration("ABC123");
        aircraft.setType("Boeing 737");
        aircraft.setRange(5000);
        aircraft.setCapacity(200);

        when(aircraftRepository.existsByRegistration(anyString())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> aircraftService.create(aircraft));
    }

    @Test
    void testGetAllAircraft() {
        Aircraft aircraft1 = new Aircraft();
        aircraft1.setRegistration("ABC123");
        aircraft1.setType("Boeing 737");
        aircraft1.setRange(5000);
        aircraft1.setCapacity(200);

        Aircraft aircraft2 = new Aircraft();
        aircraft2.setRegistration("DEF456");
        aircraft2.setType("Airbus A320");
        aircraft2.setRange(4000);
        aircraft2.setCapacity(180);

        List<Aircraft> aircraftList = Arrays.asList(aircraft1, aircraft2);

        when(aircraftRepository.findAll()).thenReturn(aircraftList);

        List<Aircraft> result = aircraftService.getAll();

        assertEquals(2, result.size());
        assertEquals("ABC123", result.get(0).getRegistration());
        assertEquals("DEF456", result.get(1).getRegistration());
    }

    @Test
    void testGetAircraftById() {
        Aircraft aircraft = new Aircraft();
        aircraft.setId(1);
        aircraft.setRegistration("ABC123");
        aircraft.setType("Boeing 737");
        aircraft.setRange(5000);
        aircraft.setCapacity(200);

        when(aircraftRepository.findById(anyInt())).thenReturn(Optional.of(aircraft));

        Aircraft result = aircraftService.getById(1);

        assertNotNull(result);
        assertEquals("ABC123", result.getRegistration());
        assertEquals("Boeing 737", result.getType());
    }

    @Test
    void testGetAircraftByIdNotFound() {
        when(aircraftRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> aircraftService.getById(1));
    }

    @Test
    void testUpdateAircraft() {
        Aircraft existingAircraft = new Aircraft();
        existingAircraft.setId(1);
        existingAircraft.setRegistration("ABC123");
        existingAircraft.setType("Boeing 737");
        existingAircraft.setRange(5000);
        existingAircraft.setCapacity(200);

        Aircraft updatedAircraft = new Aircraft();
        updatedAircraft.setRegistration("DEF456");
        updatedAircraft.setType("Airbus A320");
        updatedAircraft.setRange(4000);
        updatedAircraft.setCapacity(180);

        when(aircraftRepository.existsByRegistration(anyString())).thenReturn(false);
        when(aircraftRepository.findById(anyInt())).thenReturn(Optional.of(existingAircraft));
        when(aircraftRepository.save(any(Aircraft.class))).thenReturn(updatedAircraft);

        Aircraft result = aircraftService.update(1, updatedAircraft);

        assertNotNull(result);
        assertEquals("DEF456", result.getRegistration());
        assertEquals("Airbus A320", result.getType());
        assertEquals(4000, result.getRange());
        assertEquals(180, result.getCapacity());
    }

    @Test
    void testUpdateAircraftWithExistingRegistration() {
        Aircraft firstAircraft = new Aircraft();
        firstAircraft.setId(1);
        firstAircraft.setRegistration("ABC123");
        firstAircraft.setType("Boeing 737");
        firstAircraft.setRange(5000);
        firstAircraft.setCapacity(200);

        Aircraft secondAircraft = new Aircraft();
        secondAircraft.setId(2);
        secondAircraft.setRegistration("DEF456");
        secondAircraft.setType("Airbus A320");
        secondAircraft.setRange(4000);
        secondAircraft.setCapacity(180);

        Aircraft updatedSecondAircraft = new Aircraft();
        updatedSecondAircraft.setRegistration("ABC123");
        updatedSecondAircraft.setType("Airbus A320");
        updatedSecondAircraft.setRange(4000);
        updatedSecondAircraft.setCapacity(180);

        when(aircraftRepository.findById(1)).thenReturn(Optional.of(firstAircraft));
        when(aircraftRepository.findById(2)).thenReturn(Optional.of(secondAircraft));
        when(aircraftRepository.existsByRegistration("ABC123")).thenReturn(true);
        when(aircraftRepository.findByRegistration("ABC123")).thenReturn(firstAircraft);

        assertThrows(BadRequestException.class, () -> aircraftService.update(2, updatedSecondAircraft));
    }

    @Test
    void testDeleteAircraft() {
        doNothing().when(aircraftRepository).deleteById(anyInt());

        aircraftService.delete(1);

        verify(aircraftRepository, times(1)).deleteById(1);
    }
}