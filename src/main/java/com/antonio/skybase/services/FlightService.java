package com.antonio.skybase.services;

import com.antonio.skybase.dtos.FlightDTO;
import com.antonio.skybase.entities.Airport;
import com.antonio.skybase.entities.Flight;
import com.antonio.skybase.exceptions.BadRequestException;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.repositories.AirportRepository;
import com.antonio.skybase.repositories.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightService {
    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private AirportRepository airportRepository;

    public Flight create(FlightDTO flightDTO) {
        if (flightRepository.existsByNumber(flightDTO.getNumber())) {
            throw new BadRequestException("Flight with number " + flightDTO.getNumber() + " already exists");
        }

        Airport departureAirport = validateAirportExists(flightDTO.getDepartureAirportId());
        Airport arrivalAirport = validateAirportExists(flightDTO.getArrivalAirportId());

        Flight flight = new Flight();
        flight.setNumber(flightDTO.getNumber());
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        flight.setDepartureTime(flightDTO.getDepartureTime());
        flight.setArrivalTime(flightDTO.getArrivalTime());
        flight.setDistance(flightDTO.getDistance());

        return flightRepository.save(flight);
    }

    public List<Flight> getAll() {
        return flightRepository.findAll();
    }

    public Flight getById(Integer id) {
        return flightRepository.findById(id).orElseThrow(() -> new NotFoundException("Flight with ID " + id + " not found"));
    }

    public Flight update(Integer id, FlightDTO flightDTO) {
        if (flightRepository.existsByNumber(flightDTO.getNumber()) && !flightRepository.findByNumber(flightDTO.getNumber()).getId().equals(id)) {
            throw new BadRequestException("Flight with number " + flightDTO.getNumber() + " already exists");
        }

        Flight flightToUpdate = flightRepository.findById(id).orElseThrow(() -> new NotFoundException("Flight with ID " + id + " not found"));

        Airport departureAirport = validateAirportExists(flightDTO.getDepartureAirportId());
        Airport arrivalAirport = validateAirportExists(flightDTO.getArrivalAirportId());

        flightToUpdate.setNumber(flightDTO.getNumber());
        flightToUpdate.setDepartureAirport(departureAirport);
        flightToUpdate.setArrivalAirport(arrivalAirport);
        flightToUpdate.setDepartureTime(flightDTO.getDepartureTime());
        flightToUpdate.setArrivalTime(flightDTO.getArrivalTime());
        flightToUpdate.setDistance(flightDTO.getDistance());

        return flightRepository.save(flightToUpdate);
    }

    public void delete(Integer id) {
        flightRepository.deleteById(id);
    }

    private Airport validateAirportExists(Integer airportId) {
        return airportRepository.findById(airportId).orElseThrow(() -> new BadRequestException("Airport with ID " + airportId + " does not exist"));
    }
}