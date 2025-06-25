package com.antonio.skybase.controllers;

import com.antonio.skybase.config.TestConfig;
import com.antonio.skybase.entities.Airport;
import com.antonio.skybase.entities.City;
import com.antonio.skybase.entities.Country;
import com.antonio.skybase.entities.Flight;
import com.antonio.skybase.repositories.AirportRepository;
import com.antonio.skybase.repositories.CityRepository;
import com.antonio.skybase.repositories.CountryRepository;
import com.antonio.skybase.repositories.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Import(TestConfig.class)
public class FlightMvcControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CountryRepository countryRepository;

    private MockMvc mockMvc;

    private Airport departureAirport;
    private Airport arrivalAirport;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Clean up data before each test
        flightRepository.deleteAll();
        airportRepository.deleteAll();
        cityRepository.deleteAll();
        countryRepository.deleteAll();

        // Set up required data
        Country country = new Country();
        country.setName("United States");
        country.setCode("US");
        country = countryRepository.save(country);

        City departureCity = new City();
        departureCity.setName("Los Angeles");
        departureCity.setCountry(country);
        departureCity = cityRepository.save(departureCity);

        City arrivalCity = new City();
        arrivalCity.setName("New York");
        arrivalCity.setCountry(country);
        arrivalCity = cityRepository.save(arrivalCity);

        departureAirport = new Airport();
        departureAirport.setCode("LAX");
        departureAirport.setName("Los Angeles International");
        departureAirport.setCity(departureCity);
        departureAirport = airportRepository.save(departureAirport);

        arrivalAirport = new Airport();
        arrivalAirport.setCode("JFK");
        arrivalAirport.setName("John F. Kennedy International");
        arrivalAirport.setCity(arrivalCity);
        arrivalAirport = airportRepository.save(arrivalAirport);
    }

    @Test
    void testShowFlightList() throws Exception {
        // Given
        Flight flight1 = new Flight();
        flight1.setNumber("AA123");
        flight1.setDepartureAirport(departureAirport);
        flight1.setArrivalAirport(arrivalAirport);
        flight1.setDepartureTime(LocalTime.of(10, 0));
        flight1.setArrivalTime(LocalTime.of(13, 0));
        flight1.setDistance(2500);
        flightRepository.save(flight1);

        Flight flight2 = new Flight();
        flight2.setNumber("UA456");
        flight2.setDepartureAirport(arrivalAirport);
        flight2.setArrivalAirport(departureAirport);
        flight2.setDepartureTime(LocalTime.of(15, 0));
        flight2.setArrivalTime(LocalTime.of(18, 0));
        flight2.setDistance(2500);
        flightRepository.save(flight2);

        // When & Then
        mockMvc.perform(get("/web/flights"))
                .andExpect(status().isOk())
                .andExpect(view().name("flights/list"))
                .andExpect(model().attributeExists("flights"));
    }

    @Test
    void testShowAddFlightForm() throws Exception {
        // When & Then
        mockMvc.perform(get("/web/flights/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("flights/form"))
                .andExpect(model().attributeExists("flightDTO"))
                .andExpect(model().attributeExists("airports"));
    }

    @Test
    void testShowEditFlightForm() throws Exception {
        // Given
        Flight flight = new Flight();
        flight.setNumber("DL789");
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        flight.setDepartureTime(LocalTime.of(8, 0));
        flight.setArrivalTime(LocalTime.of(11, 0));
        flight.setDistance(2500);
        Flight saved = flightRepository.save(flight);

        // When & Then
        mockMvc.perform(get("/web/flights/{id}/edit", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("flights/form"))
                .andExpect(model().attributeExists("flightDTO"))
                .andExpect(model().attributeExists("airports"));
    }

    @Test
    void testShowFlightDetails() throws Exception {
        // Given
        Flight flight = new Flight();
        flight.setNumber("SW101");
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        flight.setDepartureTime(LocalTime.of(12, 0));
        flight.setArrivalTime(LocalTime.of(15, 0));
        flight.setDistance(2500);
        Flight saved = flightRepository.save(flight);

        // When & Then
        mockMvc.perform(get("/web/flights/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("flights/details"))
                .andExpect(model().attributeExists("flight"));
    }

    @Test
    void testCreateFlightSuccess() throws Exception {
        // When & Then
        mockMvc.perform(post("/web/flights")
                .param("number", "AA999")
                .param("departureAirportId", departureAirport.getId().toString())
                .param("arrivalAirportId", arrivalAirport.getId().toString())
                .param("departureTime", "09:00")
                .param("arrivalTime", "12:00")
                .param("distance", "2500"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/flights"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify flight was created
        Flight fromDb = flightRepository.findByNumber("AA999");
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getDepartureAirport().getId()).isEqualTo(departureAirport.getId());
        assertThat(fromDb.getArrivalAirport().getId()).isEqualTo(arrivalAirport.getId());
        assertThat(fromDb.getDepartureTime()).isEqualTo(LocalTime.of(9, 0));
        assertThat(fromDb.getArrivalTime()).isEqualTo(LocalTime.of(12, 0));
        assertThat(fromDb.getDistance()).isEqualTo(2500);
    }

    @Test
    void testCreateFlightValidationError() throws Exception {
        // When & Then - Missing required fields
        mockMvc.perform(post("/web/flights")
                .param("number", "") // Empty number
                .param("departureAirportId", departureAirport.getId().toString())
                .param("arrivalAirportId", arrivalAirport.getId().toString())
                .param("departureTime", "09:00")
                .param("arrivalTime", "12:00")
                .param("distance", "2500"))
                .andExpect(status().isOk())
                .andExpect(view().name("flights/form"))
                .andExpect(model().attributeExists("flightDTO"))
                .andExpect(model().attributeExists("airports"))
                .andExpect(model().hasErrors());

        // Verify no flight was created
        assertThat(flightRepository.findAll()).isEmpty();
    }

    @Test
    void testUpdateFlightSuccess() throws Exception {
        // Given
        Flight existingFlight = new Flight();
        existingFlight.setNumber("UA777");
        existingFlight.setDepartureAirport(departureAirport);
        existingFlight.setArrivalAirport(arrivalAirport);
        existingFlight.setDepartureTime(LocalTime.of(14, 0));
        existingFlight.setArrivalTime(LocalTime.of(17, 0));
        existingFlight.setDistance(2500);
        Flight saved = flightRepository.save(existingFlight);

        // When & Then
        mockMvc.perform(post("/web/flights/{id}", saved.getId())
                .param("number", "UA777-Updated")
                .param("departureAirportId", arrivalAirport.getId().toString())
                .param("arrivalAirportId", departureAirport.getId().toString())
                .param("departureTime", "16:00")
                .param("arrivalTime", "19:00")
                .param("distance", "2600"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/flights"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify flight was updated
        Flight fromDb = flightRepository.findById(saved.getId()).orElse(null);
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getNumber()).isEqualTo("UA777-Updated");
        assertThat(fromDb.getDepartureAirport().getId()).isEqualTo(arrivalAirport.getId());
        assertThat(fromDb.getArrivalAirport().getId()).isEqualTo(departureAirport.getId());
        assertThat(fromDb.getDepartureTime()).isEqualTo(LocalTime.of(16, 0));
        assertThat(fromDb.getArrivalTime()).isEqualTo(LocalTime.of(19, 0));
        assertThat(fromDb.getDistance()).isEqualTo(2600);
    }

    @Test
    void testDeleteFlightSuccess() throws Exception {
        // Given
        Flight flight = new Flight();
        flight.setNumber("DL555");
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        flight.setDepartureTime(LocalTime.of(20, 0));
        flight.setArrivalTime(LocalTime.of(23, 0));
        flight.setDistance(2500);
        Flight saved = flightRepository.save(flight);

        // When & Then
        mockMvc.perform(post("/web/flights/{id}/delete", saved.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/flights"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify flight was deleted
        assertThat(flightRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testShowFlightNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/web/flights/{id}", 9999))
                .andExpect(status().isOk())
                .andExpect(view().name("flights/list"))
                .andExpect(model().attributeExists("flights"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void testEditFlightNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/web/flights/{id}/edit", 9999))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/flights"));
    }

    @Test
    void testDeleteFlightNotFound() throws Exception {
        // When & Then - Should still redirect with success (idempotent delete)
        mockMvc.perform(post("/web/flights/{id}/delete", 9999))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/flights"))
                .andExpect(flash().attributeExists("successMessage"));
    }
}
