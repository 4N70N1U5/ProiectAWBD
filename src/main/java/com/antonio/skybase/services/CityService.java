package com.antonio.skybase.services;

import com.antonio.skybase.dtos.CityDTO;
import com.antonio.skybase.entities.City;
import com.antonio.skybase.entities.Country;
import com.antonio.skybase.exceptions.BadRequestException;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.repositories.CityRepository;
import com.antonio.skybase.repositories.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {
    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CountryRepository countryRepository;

    public City create(CityDTO cityDTO) {
        Country country = validateCountryExists(cityDTO.getCountryId());
        City city = new City();
        city.setName(cityDTO.getName());
        city.setCountry(country);
        return cityRepository.save(city);
    }

    public List<City> getAll() {
        return cityRepository.findAll();
    }

    public City getById(Integer id) {
        return cityRepository.findById(id).orElseThrow(() -> new NotFoundException("City with ID " + id + " not found"));
    }

    public City update(Integer id, CityDTO cityDTO) {
        City cityToUpdate = cityRepository.findById(id).orElseThrow(() -> new NotFoundException("City with ID " + id + " not found"));
        Country country = validateCountryExists(cityDTO.getCountryId());
        cityToUpdate.setName(cityDTO.getName());
        cityToUpdate.setCountry(country);
        return cityRepository.save(cityToUpdate);
    }

    public void delete(Integer id) {
        cityRepository.deleteById(id);
    }

    private Country validateCountryExists(Integer countryId) {
        return countryRepository.findById(countryId).orElseThrow(() -> new BadRequestException("Country with ID " + countryId + " does not exist"));
    }
}