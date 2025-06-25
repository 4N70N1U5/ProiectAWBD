package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.AircraftAssignmentDTO;
import com.antonio.skybase.entities.AircraftAssignment;
import com.antonio.skybase.entities.AircraftAssignmentId;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.services.AircraftAssignmentService;
import com.antonio.skybase.services.AircraftService;
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
@RequestMapping("/web/aircraft-assignments")
public class AircraftAssignmentMvcController {

    @Autowired
    private AircraftAssignmentService aircraftAssignmentService;

    @Autowired
    private AircraftService aircraftService;

    @Autowired
    private FlightService flightService;

    // Display list of all aircraft assignments
    @GetMapping
    public String getAllAircraftAssignments(Model model) {
        List<AircraftAssignment> assignments = aircraftAssignmentService.getAll();
        model.addAttribute("assignments", assignments);
        return "aircraft-assignments/list";
    }

    // Show form to create a new aircraft assignment
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("aircraftAssignmentDTO", new AircraftAssignmentDTO());
        model.addAttribute("aircraft", aircraftService.getAll());
        model.addAttribute("flights", flightService.getAll());
        return "aircraft-assignments/form";
    }

    // Handle aircraft assignment creation
    @PostMapping
    public String createAircraftAssignment(
            @Valid @ModelAttribute("aircraftAssignmentDTO") AircraftAssignmentDTO aircraftAssignmentDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("aircraft", aircraftService.getAll());
            model.addAttribute("flights", flightService.getAll());
            return "aircraft-assignments/form";
        }

        try {
            aircraftAssignmentService.create(aircraftAssignmentDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Aircraft assignment created successfully!");
        } catch (Exception e) {
            model.addAttribute("aircraft", aircraftService.getAll());
            model.addAttribute("flights", flightService.getAll());
            model.addAttribute("errorMessage", "Error creating aircraft assignment: " + e.getMessage());
            return "aircraft-assignments/form";
        }

        return "redirect:/web/aircraft-assignments";
    }

    // Show aircraft assignment details
    @GetMapping("/{aircraftId}/{flightId}/{date}")
    public String getAircraftAssignmentById(@PathVariable("aircraftId") Integer aircraftId,
            @PathVariable("flightId") Integer flightId,
            @PathVariable("date") String date,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            AircraftAssignmentId id = new AircraftAssignmentId();
            id.setAircraftId(aircraftId);
            id.setFlightId(flightId);
            id.setDate(java.time.LocalDate.parse(date));

            AircraftAssignment assignment = aircraftAssignmentService.getById(id);
            model.addAttribute("assignment", assignment);
            return "aircraft-assignments/details";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Aircraft assignment not found");
            return "redirect:/web/aircraft-assignments";
        }
    }

    // Show form to edit an existing aircraft assignment
    @GetMapping("/{aircraftId}/{flightId}/{date}/edit")
    public String showEditForm(@PathVariable("aircraftId") Integer aircraftId,
            @PathVariable("flightId") Integer flightId,
            @PathVariable("date") String date,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            AircraftAssignmentId id = new AircraftAssignmentId();
            id.setAircraftId(aircraftId);
            id.setFlightId(flightId);
            id.setDate(java.time.LocalDate.parse(date));

            AircraftAssignment assignment = aircraftAssignmentService.getById(id);

            AircraftAssignmentDTO aircraftAssignmentDTO = new AircraftAssignmentDTO();
            aircraftAssignmentDTO.setAircraftId(assignment.getId().getAircraftId());
            aircraftAssignmentDTO.setFlightId(assignment.getId().getFlightId());
            aircraftAssignmentDTO.setDate(assignment.getId().getDate());

            model.addAttribute("aircraftAssignmentDTO", aircraftAssignmentDTO);
            model.addAttribute("originalId", id);
            model.addAttribute("aircraft", aircraftService.getAll());
            model.addAttribute("flights", flightService.getAll());
            return "aircraft-assignments/form";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Aircraft assignment not found");
            return "redirect:/web/aircraft-assignments";
        }
    }

    // Handle aircraft assignment update
    @PostMapping("/{aircraftId}/{flightId}/{date}")
    public String updateAircraftAssignment(@PathVariable("aircraftId") Integer aircraftId,
            @PathVariable("flightId") Integer flightId,
            @PathVariable("date") String date,
            @Valid @ModelAttribute("aircraftAssignmentDTO") AircraftAssignmentDTO aircraftAssignmentDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            AircraftAssignmentId originalId = new AircraftAssignmentId();
            originalId.setAircraftId(aircraftId);
            originalId.setFlightId(flightId);
            originalId.setDate(java.time.LocalDate.parse(date));

            model.addAttribute("originalId", originalId);
            model.addAttribute("aircraft", aircraftService.getAll());
            model.addAttribute("flights", flightService.getAll());
            return "aircraft-assignments/form";
        }

        try {
            AircraftAssignmentId originalId = new AircraftAssignmentId();
            originalId.setAircraftId(aircraftId);
            originalId.setFlightId(flightId);
            originalId.setDate(java.time.LocalDate.parse(date));

            aircraftAssignmentService.update(originalId, aircraftAssignmentDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Aircraft assignment updated successfully!");
        } catch (Exception e) {
            AircraftAssignmentId originalId = new AircraftAssignmentId();
            originalId.setAircraftId(aircraftId);
            originalId.setFlightId(flightId);
            originalId.setDate(java.time.LocalDate.parse(date));

            model.addAttribute("originalId", originalId);
            model.addAttribute("aircraft", aircraftService.getAll());
            model.addAttribute("flights", flightService.getAll());
            model.addAttribute("errorMessage", "Error updating aircraft assignment: " + e.getMessage());
            return "aircraft-assignments/form";
        }

        return "redirect:/web/aircraft-assignments";
    }

    // Handle aircraft assignment deletion
    @PostMapping("/{aircraftId}/{flightId}/{date}/delete")
    public String deleteAircraftAssignment(@PathVariable("aircraftId") Integer aircraftId,
            @PathVariable("flightId") Integer flightId,
            @PathVariable("date") String date,
            RedirectAttributes redirectAttributes) {
        try {
            AircraftAssignmentId id = new AircraftAssignmentId();
            id.setAircraftId(aircraftId);
            id.setFlightId(flightId);
            id.setDate(java.time.LocalDate.parse(date));

            aircraftAssignmentService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Aircraft assignment deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error deleting aircraft assignment: " + e.getMessage());
        }

        return "redirect:/web/aircraft-assignments";
    }
}
