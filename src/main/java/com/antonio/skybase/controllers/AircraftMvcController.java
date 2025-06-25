package com.antonio.skybase.controllers;

import com.antonio.skybase.entities.Aircraft;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.services.AircraftService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/web/aircraft")
public class AircraftMvcController {

    @Autowired
    private AircraftService aircraftService;

    // Display list of all aircraft
    @GetMapping
    public String getAllAircraft(Model model) {
        List<Aircraft> aircraft = aircraftService.getAll();
        model.addAttribute("aircraft", aircraft);
        return "aircraft/list";
    }

    // Show form to create a new aircraft
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("aircraft", new Aircraft());
        return "aircraft/form";
    }

    // Handle aircraft creation
    @PostMapping
    public String createAircraft(@Valid @ModelAttribute("aircraft") Aircraft aircraft,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "aircraft/form";
        }

        try {
            aircraftService.create(aircraft);
            redirectAttributes.addFlashAttribute("successMessage", "Aircraft created successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error creating aircraft: " + e.getMessage());
            return "aircraft/form";
        }

        return "redirect:/web/aircraft";
    }

    // Show aircraft details
    @GetMapping("/{id}")
    public String getAircraftById(@PathVariable("id") Integer id, Model model) {
        try {
            Aircraft aircraft = aircraftService.getById(id);
            model.addAttribute("aircraft", aircraft);
            return "aircraft/details";
        } catch (NotFoundException e) {
            model.addAttribute("errorMessage", "Aircraft not found");
            return "aircraft/list";
        }
    }

    // Show form to edit an existing aircraft
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        try {
            Aircraft aircraft = aircraftService.getById(id);
            model.addAttribute("aircraft", aircraft);
            return "aircraft/form";
        } catch (NotFoundException e) {
            model.addAttribute("errorMessage", "Aircraft not found");
            return "redirect:/web/aircraft";
        }
    }

    // Handle aircraft update
    @PostMapping("/{id}")
    public String updateAircraft(@PathVariable("id") Integer id,
            @Valid @ModelAttribute("aircraft") Aircraft aircraft,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "aircraft/form";
        }

        try {
            aircraftService.update(id, aircraft);
            redirectAttributes.addFlashAttribute("successMessage", "Aircraft updated successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating aircraft: " + e.getMessage());
            return "aircraft/form";
        }

        return "redirect:/web/aircraft";
    }

    // Handle aircraft deletion
    @PostMapping("/{id}/delete")
    public String deleteAircraft(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            aircraftService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Aircraft deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting aircraft: " + e.getMessage());
        }

        return "redirect:/web/aircraft";
    }
}
