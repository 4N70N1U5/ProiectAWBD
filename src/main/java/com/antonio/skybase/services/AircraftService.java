package com.antonio.skybase.services;

import com.antonio.skybase.entities.Aircraft;
import com.antonio.skybase.entities.AircraftAssignment;
import com.antonio.skybase.exceptions.BadRequestException;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.repositories.AircraftAssignmentRepository;
import com.antonio.skybase.repositories.AircraftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AircraftService {
    @Autowired
    private AircraftRepository aircraftRepository;

    @Autowired
    private AircraftAssignmentRepository aircraftAssignmentRepository;

    public Aircraft create(Aircraft aircraft) {
        if (aircraftRepository.existsByRegistration(aircraft.getRegistration())) {
            throw new BadRequestException("Aircraft with registration " + aircraft.getRegistration() + " already exists");
        }

        return aircraftRepository.save(aircraft);
    }

    public List<Aircraft> getAll() {
        return aircraftRepository.findAll();
    }

    public Aircraft getById(Integer id) {
        return aircraftRepository.findById(id).orElseThrow(() -> new NotFoundException("Aircraft with ID " + id + " not found"));
    }

    public List<Aircraft> getAvailableAircraftByDate(LocalDate date) {
        return aircraftRepository.findAvailableAircraftByDate(date);
    }

    public List<LocalDate> getAircraftAvailabilities(Integer id, LocalDate startDate, LocalDate endDate) {
        List<AircraftAssignment> aircraftAssignments = aircraftAssignmentRepository.findByIdAircraftIdAndIdDateBetween(id, startDate, endDate);

        List<LocalDate> assignedDates = aircraftAssignments.stream()
                .map(assignment -> assignment.getId().getDate())
                .toList();

        return Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(startDate, endDate) + 1)
                .filter(date -> !assignedDates.contains(date))
                .collect(Collectors.toList());
    }

    public Aircraft update(Integer id, Aircraft aircraft) {
        if (aircraftRepository.existsByRegistration(aircraft.getRegistration()) && !aircraftRepository.findByRegistration(aircraft.getRegistration()).getId().equals(id)) {
            throw new BadRequestException("Aircraft with registration " + aircraft.getRegistration() + " already exists");
        }

        Aircraft aircraftToUpdate = aircraftRepository.findById(id).orElseThrow(() -> new NotFoundException("Aircraft with ID " + id + " not found"));
        aircraftToUpdate.setRegistration(aircraft.getRegistration());
        aircraftToUpdate.setType(aircraft.getType());
        aircraftToUpdate.setRange(aircraft.getRange());
        aircraftToUpdate.setCapacity(aircraft.getCapacity());
        return aircraftRepository.save(aircraftToUpdate);
    }

    public void delete(Integer id) {
        aircraftRepository.deleteById(id);
    }
}