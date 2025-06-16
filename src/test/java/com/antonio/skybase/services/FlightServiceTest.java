package com.antonio.skybase.services;

import com.antonio.skybase.dtos.FlightDTO;
import com.antonio.skybase.entities.Airport;
import com.antonio.skybase.entities.Flight;
import com.antonio.skybase.exceptions.BadRequestException;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.repositories.AirportRepository;
import com.antonio.skybase.repositories.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private AirportRepository airportRepository;

    @InjectMocks
    private FlightService flightService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateFlight() {
        FlightDTO flightDTO = new FlightDTO();
        flightDTO.setNumber("FL123");
        flightDTO.setDepartureAirportId(1);
        flightDTO.setArrivalAirportId(2);
        flightDTO.setDepartureTime(LocalTime.of(10, 0));
        flightDTO.setArrivalTime(LocalTime.of(12, 0));
        flightDTO.setDistance(500);

        Airport departureAirport = new Airport();
        departureAirport.setId(1);
        departureAirport.setName("Departure Airport");

        Airport arrivalAirport = new Airport();
        arrivalAirport.setId(2);
        arrivalAirport.setName("Arrival Airport");

        Flight flight = new Flight();
        flight.setId(1);
        flight.setNumber("FL123");
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        flight.setDepartureTime(LocalTime.of(10, 0));
        flight.setArrivalTime(LocalTime.of(12, 0));
        flight.setDistance(500);

        when(airportRepository.findById(1)).thenReturn(Optional.of(departureAirport));
        when(airportRepository.findById(2)).thenReturn(Optional.of(arrivalAirport));
        when(flightRepository.save(any(Flight.class))).thenReturn(flight);

        Flight createdFlight = flightService.create(flightDTO);

        assertNotNull(createdFlight);
        assertEquals("FL123", createdFlight.getNumber());
        assertEquals(departureAirport, createdFlight.getDepartureAirport());
        assertEquals(arrivalAirport, createdFlight.getArrivalAirport());
        assertEquals(LocalTime.of(10, 0), createdFlight.getDepartureTime());
        assertEquals(LocalTime.of(12, 0), createdFlight.getArrivalTime());
        assertEquals(500, createdFlight.getDistance());
    }

    @Test
    void testCreateFlightWithInvalidDepartureAirport() {
        FlightDTO flightDTO = new FlightDTO();
        flightDTO.setNumber("FL123");
        flightDTO.setDepartureAirportId(1);
        flightDTO.setArrivalAirportId(2);
        flightDTO.setDepartureTime(LocalTime.of(10, 0));
        flightDTO.setArrivalTime(LocalTime.of(12, 0));
        flightDTO.setDistance(500);

        when(airportRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> flightService.create(flightDTO));
    }

    @Test
    void testGetAllFlights() {
        Airport departureAirport = new Airport();
        departureAirport.setId(1);
        departureAirport.setName("Departure Airport");

        Airport arrivalAirport = new Airport();
        arrivalAirport.setId(2);
        arrivalAirport.setName("Arrival Airport");

        Flight flight1 = new Flight();
        flight1.setId(1);
        flight1.setNumber("FL123");
        flight1.setDepartureAirport(departureAirport);
        flight1.setArrivalAirport(arrivalAirport);
        flight1.setDepartureTime(LocalTime.of(10, 0));
        flight1.setArrivalTime(LocalTime.of(12, 0));
        flight1.setDistance(500);

        Flight flight2 = new Flight();
        flight2.setId(2);
        flight2.setNumber("FL124");
        flight2.setDepartureAirport(departureAirport);
        flight2.setArrivalAirport(arrivalAirport);
        flight2.setDepartureTime(LocalTime.of(14, 0));
        flight2.setArrivalTime(LocalTime.of(16, 0));
        flight2.setDistance(600);

        List<Flight> flights = Arrays.asList(flight1, flight2);

        when(flightRepository.findAll()).thenReturn(flights);

        List<Flight> result = flightService.getAll();

        assertEquals(2, result.size());
        assertEquals("FL123", result.get(0).getNumber());
        assertEquals("FL124", result.get(1).getNumber());
    }

    @Test
    void testGetFlightById() {
        Airport departureAirport = new Airport();
        departureAirport.setId(1);
        departureAirport.setName("Departure Airport");

        Airport arrivalAirport = new Airport();
        arrivalAirport.setId(2);
        arrivalAirport.setName("Arrival Airport");

        Flight flight = new Flight();
        flight.setId(1);
        flight.setNumber("FL123");
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        flight.setDepartureTime(LocalTime.of(10, 0));
        flight.setArrivalTime(LocalTime.of(12, 0));
        flight.setDistance(500);

        when(flightRepository.findById(anyInt())).thenReturn(Optional.of(flight));

        Flight result = flightService.getById(1);

        assertNotNull(result);
        assertEquals("FL123", result.getNumber());
        assertEquals(departureAirport, result.getDepartureAirport());
        assertEquals(arrivalAirport, result.getArrivalAirport());
    }

    @Test
    void testGetFlightByIdNotFound() {
        when(flightRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> flightService.getById(1));
    }

    @Test
    void testUpdateFlight() {
        Airport departureAirport = new Airport();
        departureAirport.setId(1);
        departureAirport.setName("Departure Airport");

        Airport arrivalAirport = new Airport();
        arrivalAirport.setId(2);
        arrivalAirport.setName("Arrival Airport");

        Flight flight = new Flight();
        flight.setId(1);
        flight.setNumber("FL123");
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        flight.setDepartureTime(LocalTime.of(10, 0));
        flight.setArrivalTime(LocalTime.of(12, 0));
        flight.setDistance(500);

        FlightDTO flightDTO = new FlightDTO();
        flightDTO.setNumber("FL123");
        flightDTO.setDepartureAirportId(1);
        flightDTO.setArrivalAirportId(2);
        flightDTO.setDepartureTime(LocalTime.of(10, 0));
        flightDTO.setArrivalTime(LocalTime.of(12, 0));
        flightDTO.setDistance(500);

        when(flightRepository.findById(anyInt())).thenReturn(Optional.of(flight));
        when(airportRepository.findById(1)).thenReturn(Optional.of(departureAirport));
        when(airportRepository.findById(2)).thenReturn(Optional.of(arrivalAirport));
        when(flightRepository.save(any(Flight.class))).thenReturn(flight);

        Flight updatedFlight = flightService.update(1, flightDTO);

        assertNotNull(updatedFlight);
        assertEquals("FL123", updatedFlight.getNumber());
        assertEquals(departureAirport, updatedFlight.getDepartureAirport());
        assertEquals(arrivalAirport, updatedFlight.getArrivalAirport());
        assertEquals(LocalTime.of(10, 0), updatedFlight.getDepartureTime());
        assertEquals(LocalTime.of(12, 0), updatedFlight.getArrivalTime());
        assertEquals(500, updatedFlight.getDistance());
    }

    @Test
    void testUpdateFlightWithInvalidDepartureAirport() {
        Flight flight = new Flight();
        flight.setId(1);
        flight.setNumber("FL123");
        flight.setDepartureTime(LocalTime.of(10, 0));
        flight.setArrivalTime(LocalTime.of(12, 0));
        flight.setDistance(500);

        FlightDTO flightDTO = new FlightDTO();
        flightDTO.setNumber("FL123");
        flightDTO.setDepartureAirportId(1);
        flightDTO.setArrivalAirportId(2);
        flightDTO.setDepartureTime(LocalTime.of(10, 0));
        flightDTO.setArrivalTime(LocalTime.of(12, 0));
        flightDTO.setDistance(500);

        when(flightRepository.findById(anyInt())).thenReturn(Optional.of(flight));
        when(airportRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> flightService.update(1, flightDTO));
    }

    @Test
    void testDeleteFlight() {
        doNothing().when(flightRepository).deleteById(anyInt());

        flightService.delete(1);

        verify(flightRepository, times(1)).deleteById(1);
    }
}