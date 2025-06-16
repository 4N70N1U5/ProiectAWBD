package com.antonio.skybase.services;

import com.antonio.skybase.entities.Country;
import com.antonio.skybase.exceptions.BadRequestException;
import com.antonio.skybase.exceptions.NotFoundException;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryService countryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCountry() {
        Country country = new Country();
        country.setName("Test Country");
        country.setCode("TC");

        when(countryRepository.existsByCode(anyString())).thenReturn(false);
        when(countryRepository.save(any(Country.class))).thenReturn(country);

        Country createdCountry = countryService.create(country);

        assertNotNull(createdCountry);
        assertEquals("Test Country", createdCountry.getName());
        assertEquals("TC", createdCountry.getCode());
    }

    @Test
    void testCreateCountryWithExistingCode() {
        Country country = new Country();
        country.setName("Test Country");
        country.setCode("TC");

        when(countryRepository.existsByCode(anyString())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> countryService.create(country));
    }

    @Test
    void testGetAllCountries() {
        Country country1 = new Country();
        country1.setName("Country 1");
        country1.setCode("C1");

        Country country2 = new Country();
        country2.setName("Country 2");
        country2.setCode("C2");

        List<Country> countries = Arrays.asList(country1, country2);

        when(countryRepository.findAll()).thenReturn(countries);

        List<Country> result = countryService.getAll();

        assertEquals(2, result.size());
        assertEquals("Country 1", result.get(0).getName());
        assertEquals("C1", result.get(0).getCode());
        assertEquals("Country 2", result.get(1).getName());
        assertEquals("C2", result.get(1).getCode());
    }

    @Test
    void testGetCountryById() {
        Country country = new Country();
        country.setId(1);
        country.setName("Test Country");
        country.setCode("TC");

        when(countryRepository.findById(anyInt())).thenReturn(Optional.of(country));

        Country result = countryService.getById(1);

        assertNotNull(result);
        assertEquals("Test Country", result.getName());
        assertEquals("TC", result.getCode());
    }

    @Test
    void testGetCountryByIdNotFound() {
        when(countryRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> countryService.getById(1));
    }

    @Test
    void testUpdateCountry() {
        Country existingCountry = new Country();
        existingCountry.setId(1);
        existingCountry.setName("Existing Country");
        existingCountry.setCode("EC");

        Country updatedCountry = new Country();
        updatedCountry.setName("Updated Country");
        updatedCountry.setCode("UC");

        when(countryRepository.existsByCode(anyString())).thenReturn(false);
        when(countryRepository.findById(anyInt())).thenReturn(Optional.of(existingCountry));
        when(countryRepository.save(any(Country.class))).thenReturn(updatedCountry);

        Country result = countryService.update(1, updatedCountry);

        assertNotNull(result);
        assertEquals("Updated Country", result.getName());
        assertEquals("UC", result.getCode());
    }

    @Test
    void testUpdateCountryWithExistingCode() {
        Country firstCountry = new Country();
        firstCountry.setId(1);
        firstCountry.setName("First Country");
        firstCountry.setCode("FC");

        Country secondCountry = new Country();
        secondCountry.setId(2);
        secondCountry.setName("Second Country");
        secondCountry.setCode("SC");

        Country updatedSecondCountry = new Country();
        updatedSecondCountry.setName("Updated Second Country");
        updatedSecondCountry.setCode("FC");

        when(countryRepository.findById(1)).thenReturn(Optional.of(firstCountry));
        when(countryRepository.findById(2)).thenReturn(Optional.of(secondCountry));
        when(countryRepository.existsByCode("FC")).thenReturn(true);
        when(countryRepository.findByCode("FC")).thenReturn(firstCountry);

        assertThrows(BadRequestException.class, () -> countryService.update(2, updatedSecondCountry));
    }

    @Test
    void testDeleteCountry() {
        doNothing().when(countryRepository).deleteById(anyInt());

        countryService.delete(1);

        verify(countryRepository, times(1)).deleteById(1);
    }
}