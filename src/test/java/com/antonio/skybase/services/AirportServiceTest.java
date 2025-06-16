package com.antonio.skybase.services;

import com.antonio.skybase.dtos.AirportDTO;
import com.antonio.skybase.entities.Airport;
import com.antonio.skybase.entities.City;
import com.antonio.skybase.exceptions.BadRequestException;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.repositories.AirportRepository;
import com.antonio.skybase.repositories.CityRepository;
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

class AirportServiceTest {

    @Mock
    private AirportRepository airportRepository;

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private AirportService airportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAirport() {
        AirportDTO airportDTO = new AirportDTO();
        airportDTO.setName("Test Airport");
        airportDTO.setCode("TST");
        airportDTO.setCityId(1);

        City city = new City();
        city.setId(1);
        city.setName("Test City");

        Airport airport = new Airport();
        airport.setId(1);
        airport.setName("Test Airport");
        airport.setCode("TST");
        airport.setCity(city);

        when(cityRepository.findById(anyInt())).thenReturn(Optional.of(city));
        when(airportRepository.existsByCode(anyString())).thenReturn(false);
        when(airportRepository.save(any(Airport.class))).thenReturn(airport);

        Airport createdAirport = airportService.create(airportDTO);

        assertNotNull(createdAirport);
        assertEquals("Test Airport", createdAirport.getName());
        assertEquals("TST", createdAirport.getCode());
        assertEquals(city, createdAirport.getCity());
    }

    @Test
    void testCreateAirportWithExistingCode() {
        AirportDTO airportDTO = new AirportDTO();
        airportDTO.setName("Test Airport");
        airportDTO.setCode("TST");
        airportDTO.setCityId(1);

        when(airportRepository.existsByCode(anyString())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> airportService.create(airportDTO));
    }

    @Test
    void testCreateAirportWithInvalidCity() {
        AirportDTO airportDTO = new AirportDTO();
        airportDTO.setName("Test Airport");
        airportDTO.setCode("TST");
        airportDTO.setCityId(1);

        when(cityRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> airportService.create(airportDTO));
    }

    @Test
    void testGetAllAirports() {
        City city = new City();
        city.setId(1);
        city.setName("Test City");

        Airport airport1 = new Airport();
        airport1.setId(1);
        airport1.setName("Airport 1");
        airport1.setCode("A1");
        airport1.setCity(city);

        Airport airport2 = new Airport();
        airport2.setId(2);
        airport2.setName("Airport 2");
        airport2.setCode("A2");
        airport2.setCity(city);

        List<Airport> airports = Arrays.asList(airport1, airport2);

        when(airportRepository.findAll()).thenReturn(airports);

        List<Airport> result = airportService.getAll();

        assertEquals(2, result.size());
        assertEquals("Airport 1", result.get(0).getName());
        assertEquals("Airport 2", result.get(1).getName());
    }

    @Test
    void testGetAirportById() {
        City city = new City();
        city.setId(1);
        city.setName("Test City");

        Airport airport = new Airport();
        airport.setId(1);
        airport.setName("Test Airport");
        airport.setCode("TST");
        airport.setCity(city);

        when(airportRepository.findById(anyInt())).thenReturn(Optional.of(airport));

        Airport result = airportService.getById(1);

        assertNotNull(result);
        assertEquals("Test Airport", result.getName());
        assertEquals("TST", result.getCode());
        assertEquals(city, result.getCity());
    }

    @Test
    void testGetAirportByIdNotFound() {
        when(airportRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> airportService.getById(1));
    }

    @Test
    void testUpdateAirport() {
        City city = new City();
        city.setId(1);
        city.setName("Test City");

        Airport airport = new Airport();
        airport.setId(1);
        airport.setName("Test Airport");
        airport.setCode("TST");
        airport.setCity(city);

        AirportDTO airportDTO = new AirportDTO();
        airportDTO.setName("Updated Airport");
        airportDTO.setCode("UPD");
        airportDTO.setCityId(1);

        when(airportRepository.findById(anyInt())).thenReturn(Optional.of(airport));
        when(cityRepository.findById(anyInt())).thenReturn(Optional.of(city));
        when(airportRepository.save(any(Airport.class))).thenReturn(airport);

        Airport updatedAirport = airportService.update(1, airportDTO);

        assertNotNull(updatedAirport);
        assertEquals("Updated Airport", updatedAirport.getName());
        assertEquals("UPD", updatedAirport.getCode());
        assertEquals(city, updatedAirport.getCity());
    }

    @Test
    void testUpdateAirportWithExistingCode() {
        City city = new City();
        city.setId(1);
        city.setName("Test City");

        Airport firstAirport = new Airport();
        firstAirport.setId(1);
        firstAirport.setName("First Airport");
        firstAirport.setCode("FST");
        firstAirport.setCity(city);

        Airport secondAirport = new Airport();
        secondAirport.setId(2);
        secondAirport.setName("Second Airport");
        secondAirport.setCode("SND");
        secondAirport.setCity(city);

        AirportDTO airportDTO = new AirportDTO();
        airportDTO.setName("Updated Second Airport");
        airportDTO.setCode("FST");
        airportDTO.setCityId(1);

        when(airportRepository.findById(1)).thenReturn(Optional.of(firstAirport));
        when(airportRepository.findById(2)).thenReturn(Optional.of(secondAirport));
        when(airportRepository.existsByCode("FST")).thenReturn(true);
        when(airportRepository.findByCode("FST")).thenReturn(firstAirport);

        assertThrows(BadRequestException.class, () -> airportService.update(2, airportDTO));
    }

    @Test
    void testDeleteAirport() {
        doNothing().when(airportRepository).deleteById(anyInt());

        airportService.delete(1);

        verify(airportRepository, times(1)).deleteById(1);
    }
}