package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.FlightDTO;
import com.antonio.skybase.entities.Flight;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.services.AirportService;
import com.antonio.skybase.services.FlightService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/web/flights")
public class FlightMvcController {

    @Autowired
    private FlightService flightService;

    @Autowired
    private AirportService airportService;

    // Display list of all flights
    @GetMapping
    public String getAllFlights(Model model) {
        List<Flight> flights = flightService.getAll();
        model.addAttribute("flights", flights);
        return "flights/list";
    }

    // Show form to create a new flight
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("flightDTO", new FlightDTO());
        model.addAttribute("airports", airportService.getAll());
        return "flights/form";
    }

    // Handle flight creation
    @PostMapping
    public String createFlight(@Valid @ModelAttribute("flightDTO") FlightDTO flightDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("airports", airportService.getAll());
            return "flights/form";
        }

        try {
            flightService.create(flightDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Flight created successfully!");
        } catch (Exception e) {
            model.addAttribute("airports", airportService.getAll());
            model.addAttribute("errorMessage", "Error creating flight: " + e.getMessage());
            return "flights/form";
        }

        return "redirect:/web/flights";
    }

    // Show flight details
    @GetMapping("/{id}")
    public String getFlightById(@PathVariable("id") Integer id, Model model) {
        try {
            Flight flight = flightService.getById(id);
            model.addAttribute("flight", flight);
            return "flights/details";
        } catch (NotFoundException e) {
            model.addAttribute("errorMessage", "Flight not found");
            List<Flight> flights = flightService.getAll();
            model.addAttribute("flights", flights);
            return "flights/list";
        }
    }

    // Show form to edit an existing flight
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Flight flight = flightService.getById(id);

            FlightDTO flightDTO = new FlightDTO();
            flightDTO.setId(flight.getId());
            flightDTO.setNumber(flight.getNumber());
            flightDTO.setDepartureAirportId(flight.getDepartureAirport().getId());
            flightDTO.setArrivalAirportId(flight.getArrivalAirport().getId());
            flightDTO.setDepartureTime(flight.getDepartureTime());
            flightDTO.setArrivalTime(flight.getArrivalTime());
            flightDTO.setDistance(flight.getDistance());

            model.addAttribute("flightDTO", flightDTO);
            model.addAttribute("airports", airportService.getAll());
            return "flights/form";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Flight not found");
            return "redirect:/web/flights";
        }
    }

    // Handle flight update
    @PostMapping("/{id}")
    public String updateFlight(@PathVariable("id") Integer id,
            @Valid @ModelAttribute("flightDTO") FlightDTO flightDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("airports", airportService.getAll());
            return "flights/form";
        }

        try {
            flightService.update(id, flightDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Flight updated successfully!");
        } catch (Exception e) {
            model.addAttribute("airports", airportService.getAll());
            model.addAttribute("errorMessage", "Error updating flight: " + e.getMessage());
            return "flights/form";
        }

        return "redirect:/web/flights";
    }

    // Handle flight deletion
    @PostMapping("/{id}/delete")
    public String deleteFlight(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            flightService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Flight deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting flight: " + e.getMessage());
        }

        return "redirect:/web/flights";
    }
}
