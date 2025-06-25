package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.AirportDTO;
import com.antonio.skybase.entities.Airport;
import com.antonio.skybase.entities.City;
import com.antonio.skybase.services.AirportService;
import com.antonio.skybase.services.CityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/web/airports")
public class AirportMvcController {

    @Autowired
    private AirportService airportService;

    @Autowired
    private CityService cityService;

    @GetMapping
    public String list(Model model) {
        List<Airport> airports = airportService.getAll();
        model.addAttribute("airports", airports);
        return "airports/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        List<City> cities = cityService.getAll();
        model.addAttribute("airportDTO", new AirportDTO());
        model.addAttribute("cities", cities);
        return "airports/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute AirportDTO airportDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            List<City> cities = cityService.getAll();
            model.addAttribute("cities", cities);
            return "airports/form";
        }

        try {
            Airport airport = airportService.create(airportDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Airport '" + airport.getName() + "' created successfully!");
            return "redirect:/web/airports";
        } catch (Exception e) {
            List<City> cities = cityService.getAll();
            model.addAttribute("cities", cities);
            model.addAttribute("errorMessage", e.getMessage());
            return "airports/form";
        }
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Integer id, Model model) {
        try {
            Airport airport = airportService.getById(id);
            model.addAttribute("airport", airport);
            return "airports/details";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/web/airports";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        try {
            Airport airport = airportService.getById(id);
            List<City> cities = cityService.getAll();

            AirportDTO airportDTO = new AirportDTO();
            airportDTO.setId(airport.getId());
            airportDTO.setName(airport.getName());
            airportDTO.setCode(airport.getCode());
            airportDTO.setCityId(airport.getCity().getId());

            model.addAttribute("airportDTO", airportDTO);
            model.addAttribute("cities", cities);
            model.addAttribute("airportId", id);
            return "airports/form";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/web/airports";
        }
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
            @Valid @ModelAttribute AirportDTO airportDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            List<City> cities = cityService.getAll();
            model.addAttribute("cities", cities);
            model.addAttribute("airportId", id);
            return "airports/form";
        }

        try {
            Airport airport = airportService.update(id, airportDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Airport '" + airport.getName() + "' updated successfully!");
            return "redirect:/web/airports";
        } catch (Exception e) {
            List<City> cities = cityService.getAll();
            model.addAttribute("cities", cities);
            model.addAttribute("airportId", id);
            model.addAttribute("errorMessage", e.getMessage());
            return "airports/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Airport airport = airportService.getById(id);
            String airportName = airport.getName();
            airportService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Airport '" + airportName + "' deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/web/airports";
    }
}
