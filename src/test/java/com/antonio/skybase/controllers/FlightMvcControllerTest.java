package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.FlightDTO;
import com.antonio.skybase.entities.Airport;
import com.antonio.skybase.entities.Flight;
import com.antonio.skybase.exceptions.BadRequestException;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.services.AirportService;
import com.antonio.skybase.services.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FlightMvcControllerTest {

    @Mock
    private FlightService flightService;

    @Mock
    private AirportService airportService;

    @InjectMocks
    private FlightMvcController flightMvcController;

    private MockMvc mockMvc;

    private Flight testFlight;
    private FlightDTO testFlightDTO;
    private List<Airport> airports;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(flightMvcController).build();

        // Set up test data
        Airport departureAirport = new Airport();
        departureAirport.setId(1);
        departureAirport.setCode("LAX");
        departureAirport.setName("Los Angeles International");

        Airport arrivalAirport = new Airport();
        arrivalAirport.setId(2);
        arrivalAirport.setCode("JFK");
        arrivalAirport.setName("John F. Kennedy International");

        airports = Arrays.asList(departureAirport, arrivalAirport);

        testFlight = new Flight();
        testFlight.setId(1);
        testFlight.setNumber("AA123");
        testFlight.setDepartureAirport(departureAirport);
        testFlight.setArrivalAirport(arrivalAirport);
        testFlight.setDepartureTime(LocalTime.of(10, 0));
        testFlight.setArrivalTime(LocalTime.of(13, 0));
        testFlight.setDistance(2500);

        testFlightDTO = new FlightDTO();
        testFlightDTO.setId(1);
        testFlightDTO.setNumber("AA123");
        testFlightDTO.setDepartureAirportId(1);
        testFlightDTO.setArrivalAirportId(2);
        testFlightDTO.setDepartureTime(LocalTime.of(10, 0));
        testFlightDTO.setArrivalTime(LocalTime.of(13, 0));
        testFlightDTO.setDistance(2500);
    }

    @Test
    void testGetAllFlights() throws Exception {
        // Given
        List<Flight> flights = Arrays.asList(testFlight);
        when(flightService.getAll()).thenReturn(flights);

        // When & Then
        mockMvc.perform(get("/web/flights"))
                .andExpect(status().isOk())
                .andExpect(view().name("flights/list"))
                .andExpect(model().attributeExists("flights"))
                .andExpect(model().attribute("flights", flights));

        verify(flightService).getAll();
    }

    @Test
    void testShowCreateForm() throws Exception {
        // Given
        when(airportService.getAll()).thenReturn(airports);

        // When & Then
        mockMvc.perform(get("/web/flights/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("flights/form"))
                .andExpect(model().attributeExists("flightDTO"))
                .andExpect(model().attributeExists("airports"))
                .andExpect(model().attribute("airports", airports));

        verify(airportService).getAll();
    }

    @Test
    void testCreateFlightSuccess() throws Exception {
        // Given
        when(flightService.create(any(FlightDTO.class))).thenReturn(testFlight);

        // When & Then
        mockMvc.perform(post("/web/flights")
                .param("number", "AA123")
                .param("departureAirportId", "1")
                .param("arrivalAirportId", "2")
                .param("departureTime", "10:00")
                .param("arrivalTime", "13:00")
                .param("distance", "2500"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/flights"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(flightService).create(any(FlightDTO.class));
    }

    @Test
    void testCreateFlightValidationErrors() throws Exception {
        // Given
        when(airportService.getAll()).thenReturn(airports);

        // When & Then - Submit form with validation errors (empty number)
        mockMvc.perform(post("/web/flights")
                .param("number", "") // Empty number
                .param("departureAirportId", "1")
                .param("arrivalAirportId", "2")
                .param("departureTime", "10:00")
                .param("arrivalTime", "13:00")
                .param("distance", "2500"))
                .andExpect(status().isOk())
                .andExpect(view().name("flights/form"))
                .andExpect(model().attributeExists("flightDTO"))
                .andExpect(model().attributeExists("airports"));

        verify(airportService).getAll();
        verify(flightService, never()).create(any(FlightDTO.class));
    }

    @Test
    void testCreateFlightServiceException() throws Exception {
        // Given
        when(airportService.getAll()).thenReturn(airports);
        when(flightService.create(any(FlightDTO.class)))
                .thenThrow(new BadRequestException("Flight number already exists"));

        // When & Then
        mockMvc.perform(post("/web/flights")
                .param("number", "AA123")
                .param("departureAirportId", "1")
                .param("arrivalAirportId", "2")
                .param("departureTime", "10:00")
                .param("arrivalTime", "13:00")
                .param("distance", "2500"))
                .andExpect(status().isOk())
                .andExpect(view().name("flights/form"))
                .andExpect(model().attributeExists("flightDTO"))
                .andExpect(model().attributeExists("airports"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(flightService).create(any(FlightDTO.class));
        verify(airportService).getAll();
    }

    @Test
    void testGetFlightById() throws Exception {
        // Given
        when(flightService.getById(1)).thenReturn(testFlight);

        // When & Then
        mockMvc.perform(get("/web/flights/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("flights/details"))
                .andExpect(model().attributeExists("flight"))
                .andExpect(model().attribute("flight", testFlight));

        verify(flightService).getById(1);
    }

    @Test
    void testGetFlightByIdNotFound() throws Exception {
        // Given
        when(flightService.getById(1)).thenThrow(new NotFoundException("Flight not found"));
        when(flightService.getAll()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/web/flights/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("flights/list"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attributeExists("flights"));

        verify(flightService).getById(1);
        verify(flightService).getAll();
    }

    @Test
    void testShowEditForm() throws Exception {
        // Given
        when(flightService.getById(1)).thenReturn(testFlight);
        when(airportService.getAll()).thenReturn(airports);

        // When & Then
        mockMvc.perform(get("/web/flights/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("flights/form"))
                .andExpect(model().attributeExists("flightDTO"))
                .andExpect(model().attributeExists("airports"));

        verify(flightService).getById(1);
        verify(airportService).getAll();
    }

    @Test
    void testShowEditFormNotFound() throws Exception {
        // Given
        when(flightService.getById(1)).thenThrow(new NotFoundException("Flight not found"));

        // When & Then
        mockMvc.perform(get("/web/flights/1/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/flights"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(flightService).getById(1);
    }

    @Test
    void testUpdateFlightSuccess() throws Exception {
        // Given
        when(flightService.update(eq(1), any(FlightDTO.class))).thenReturn(testFlight);

        // When & Then
        mockMvc.perform(post("/web/flights/1")
                .param("number", "AA123")
                .param("departureAirportId", "1")
                .param("arrivalAirportId", "2")
                .param("departureTime", "10:00")
                .param("arrivalTime", "13:00")
                .param("distance", "2500"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/flights"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(flightService).update(eq(1), any(FlightDTO.class));
    }

    @Test
    void testUpdateFlightValidationErrors() throws Exception {
        // Given
        when(airportService.getAll()).thenReturn(airports);

        // When & Then - Submit form with validation errors
        mockMvc.perform(post("/web/flights/1")
                .param("number", "") // Empty number
                .param("departureAirportId", "1")
                .param("arrivalAirportId", "2")
                .param("departureTime", "10:00")
                .param("arrivalTime", "13:00")
                .param("distance", "2500"))
                .andExpect(status().isOk())
                .andExpect(view().name("flights/form"))
                .andExpect(model().attributeExists("flightDTO"))
                .andExpect(model().attributeExists("airports"));

        verify(airportService).getAll();
        verify(flightService, never()).update(anyInt(), any(FlightDTO.class));
    }

    @Test
    void testUpdateFlightServiceException() throws Exception {
        // Given
        when(airportService.getAll()).thenReturn(airports);
        when(flightService.update(eq(1), any(FlightDTO.class)))
                .thenThrow(new BadRequestException("Invalid flight data"));

        // When & Then
        mockMvc.perform(post("/web/flights/1")
                .param("number", "AA123")
                .param("departureAirportId", "1")
                .param("arrivalAirportId", "2")
                .param("departureTime", "10:00")
                .param("arrivalTime", "13:00")
                .param("distance", "2500"))
                .andExpect(status().isOk())
                .andExpect(view().name("flights/form"))
                .andExpect(model().attributeExists("flightDTO"))
                .andExpect(model().attributeExists("airports"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(flightService).update(eq(1), any(FlightDTO.class));
        verify(airportService).getAll();
    }

    @Test
    void testDeleteFlightSuccess() throws Exception {
        // Given
        doNothing().when(flightService).delete(1);

        // When & Then
        mockMvc.perform(post("/web/flights/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/flights"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(flightService).delete(1);
    }

    @Test
    void testDeleteFlightServiceException() throws Exception {
        // Given
        doThrow(new BadRequestException("Cannot delete flight with existing bookings")).when(flightService).delete(1);

        // When & Then
        mockMvc.perform(post("/web/flights/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/flights"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(flightService).delete(1);
    }
}