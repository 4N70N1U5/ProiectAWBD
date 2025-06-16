package com.antonio.skybase.services;

import com.antonio.skybase.entities.Country;
import com.antonio.skybase.exceptions.BadRequestException;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.repositories.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {
    @Autowired
    private CountryRepository countryRepository;

    public Country create(Country country) {
        if (countryRepository.existsByCode(country.getCode())) {
            throw new BadRequestException("Country with code " + country.getCode() + " already exists");
        }

        return countryRepository.save(country);
    }

    public List<Country> getAll() {
        return countryRepository.findAll();
    }

    public Country getById(Integer id) {
        return countryRepository.findById(id).orElseThrow(() -> new NotFoundException("Country with ID " + id + " not found"));
    }

    public Country update(Integer id, Country country) {
        if (countryRepository.existsByCode(country.getCode()) && !countryRepository.findByCode(country.getCode()).getId().equals(id)) {
            throw new BadRequestException("Country with code " + country.getCode() + " already exists");
        }

        Country countryToUpdate = countryRepository.findById(id).orElseThrow(() -> new NotFoundException("Country with ID " + id + " not found"));

        countryToUpdate.setName(country.getName());
        countryToUpdate.setCode(country.getCode());

        return countryRepository.save(countryToUpdate);
    }

    public void delete(Integer id) {
        countryRepository.deleteById(id);
    }
}
