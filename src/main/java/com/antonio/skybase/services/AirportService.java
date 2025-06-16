package com.antonio.skybase.services;

import com.antonio.skybase.dtos.AirportDTO;
import com.antonio.skybase.entities.Airport;
import com.antonio.skybase.entities.City;
import com.antonio.skybase.exceptions.BadRequestException;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.repositories.AirportRepository;
import com.antonio.skybase.repositories.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AirportService {
    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private CityRepository cityRepository;

    public Airport create(AirportDTO airportDTO) {
        if (airportRepository.existsByCode(airportDTO.getCode())) {
            throw new BadRequestException("Airport with code " + airportDTO.getCode() + " already exists");
        }

        City city = validateCityExists(airportDTO.getCityId());

        Airport airport = new Airport();
        airport.setName(airportDTO.getName());
        airport.setCode(airportDTO.getCode());
        airport.setCity(city);

        return airportRepository.save(airport);
    }

    public List<Airport> getAll() {
        return airportRepository.findAll();
    }

    public Airport getById(Integer id) {
        return airportRepository.findById(id).orElseThrow(() -> new NotFoundException("Airport with ID " + id + " not found"));
    }

    public Airport update(Integer id, AirportDTO airportDTO) {
        if (airportRepository.existsByCode(airportDTO.getCode()) && !airportRepository.findByCode(airportDTO.getCode()).getId().equals(id)) {
            throw new BadRequestException("Airport with code " + airportDTO.getCode() + " already exists");
        }

        Airport airportToUpdate = airportRepository.findById(id).orElseThrow(() -> new NotFoundException("Airport with ID " + id + " not found"));

        City city = validateCityExists(airportDTO.getCityId());

        airportToUpdate.setName(airportDTO.getName());
        airportToUpdate.setCode(airportDTO.getCode());
        airportToUpdate.setCity(city);

        return airportRepository.save(airportToUpdate);
    }

    public void delete(Integer id) {
        airportRepository.deleteById(id);
    }

    private City validateCityExists(Integer cityId) {
        return cityRepository.findById(cityId).orElseThrow(() -> new BadRequestException("City with ID " + cityId + " does not exist"));
    }
}