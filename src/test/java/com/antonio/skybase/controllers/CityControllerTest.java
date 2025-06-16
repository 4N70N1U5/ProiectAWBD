package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.CityDTO;
import com.antonio.skybase.entities.City;
import com.antonio.skybase.services.CityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CityControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CityService cityService;

    @InjectMocks
    private CityController cityController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(cityController).build();
    }

    @Test
    void testCreateCity() throws Exception {
        City city = new City();
        city.setId(1);
        city.setName("Test City");

        when(cityService.create(any(CityDTO.class))).thenReturn(city);

        mockMvc.perform(post("/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test City\", \"countryId\": 1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test City"));
    }

    @Test
    void testCreateCityWithBlankName() throws Exception {
        mockMvc.perform(post("/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\", \"countryId\": 1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateCityWithNullCountryId() throws Exception {
        mockMvc.perform(post("/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test City\", \"countryId\": null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllCities() throws Exception {
        City city1 = new City();
        city1.setId(1);
        city1.setName("City 1");

        City city2 = new City();
        city2.setId(2);
        city2.setName("City 2");

        when(cityService.getAll()).thenReturn(List.of(city1, city2));

        mockMvc.perform(get("/cities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("City 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("City 2"));
    }

    @Test
    void testGetCityById() throws Exception {
        City city = new City();
        city.setId(1);
        city.setName("Test City");

        when(cityService.getById(anyInt())).thenReturn(city);

        mockMvc.perform(get("/cities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test City"));
    }

    @Test
    void testUpdateCity() throws Exception {
        City city = new City();
        city.setId(1);
        city.setName("Updated City");

        when(cityService.update(anyInt(), any(CityDTO.class))).thenReturn(city);

        mockMvc.perform(put("/cities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Updated City\", \"countryId\": 1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated City"));
    }

    @Test
    void testDeleteCity() throws Exception {
        mockMvc.perform(delete("/cities/1"))
                .andExpect(status().isNoContent());
    }
}