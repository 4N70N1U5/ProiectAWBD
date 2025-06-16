package com.antonio.skybase.services;

import com.antonio.skybase.dtos.CityDTO;
import com.antonio.skybase.entities.City;
import com.antonio.skybase.entities.Country;
import com.antonio.skybase.exceptions.BadRequestException;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.repositories.CityRepository;
import com.antonio.skybase.repositories.CountryRepository;
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
import static org.mockito.Mockito.*;

class CityServiceTest {

    @Mock
    private CityRepository cityRepository;

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CityService cityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCity() {
        CityDTO cityDTO = new CityDTO();
        cityDTO.setName("Test City");
        cityDTO.setCountryId(1);

        Country country = new Country();
        country.setId(1);
        country.setName("Test Country");
        country.setCode("TC");

        City city = new City();
        city.setId(1);
        city.setName("Test City");
        city.setCountry(country);

        when(countryRepository.findById(anyInt())).thenReturn(Optional.of(country));
        when(cityRepository.save(any(City.class))).thenReturn(city);

        City createdCity = cityService.create(cityDTO);

        assertNotNull(createdCity);
        assertEquals("Test City", createdCity.getName());
        assertEquals(country, createdCity.getCountry());
    }

    @Test
    void testCreateCityWithInvalidCountry() {
        CityDTO cityDTO = new CityDTO();
        cityDTO.setName("Test City");
        cityDTO.setCountryId(1);

        when(countryRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> cityService.create(cityDTO));
    }

    @Test
    void testGetAllCities() {
        Country country = new Country();
        country.setId(1);
        country.setName("Test Country");
        country.setCode("TC");

        City city1 = new City();
        city1.setId(1);
        city1.setName("City 1");
        city1.setCountry(country);

        City city2 = new City();
        city2.setId(2);
        city2.setName("City 2");
        city2.setCountry(country);

        List<City> cities = Arrays.asList(city1, city2);

        when(cityRepository.findAll()).thenReturn(cities);

        List<City> result = cityService.getAll();

        assertEquals(2, result.size());
        assertEquals("City 1", result.get(0).getName());
        assertEquals("City 2", result.get(1).getName());
    }

    @Test
    void testGetCityById() {
        Country country = new Country();
        country.setId(1);
        country.setName("Test Country");
        country.setCode("TC");

        City city = new City();
        city.setId(1);
        city.setName("Test City");
        city.setCountry(country);

        when(cityRepository.findById(anyInt())).thenReturn(Optional.of(city));

        City result = cityService.getById(1);

        assertNotNull(result);
        assertEquals("Test City", result.getName());
        assertEquals(country, result.getCountry());
    }

    @Test
    void testGetCityByIdNotFound() {
        when(cityRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cityService.getById(1));
    }

    @Test
    void testUpdateCity() {
        Country country = new Country();
        country.setId(1);
        country.setName("Test Country");
        country.setCode("TC");

        City city = new City();
        city.setId(1);
        city.setName("Test City");
        city.setCountry(country);

        CityDTO cityDTO = new CityDTO();
        cityDTO.setName("Updated City");
        cityDTO.setCountryId(1);

        when(cityRepository.findById(anyInt())).thenReturn(Optional.of(city));
        when(countryRepository.findById(anyInt())).thenReturn(Optional.of(country));
        when(cityRepository.save(any(City.class))).thenReturn(city);

        City updatedCity = cityService.update(1, cityDTO);

        assertNotNull(updatedCity);
        assertEquals("Updated City", updatedCity.getName());
        assertEquals(country, updatedCity.getCountry());
    }

    @Test
    void testUpdateCityWithInvalidCountry() {
        City city = new City();
        city.setId(1);
        city.setName("Test City");

        CityDTO cityDTO = new CityDTO();
        cityDTO.setName("Updated City");
        cityDTO.setCountryId(1);

        when(cityRepository.findById(anyInt())).thenReturn(Optional.of(city));
        when(countryRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> cityService.update(1, cityDTO));
    }

    @Test
    void testDeleteCity() {
        doNothing().when(cityRepository).deleteById(anyInt());

        cityService.delete(1);

        verify(cityRepository, times(1)).deleteById(1);
    }
}