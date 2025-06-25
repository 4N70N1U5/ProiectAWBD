package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.CityDTO;
import com.antonio.skybase.entities.City;
import com.antonio.skybase.entities.Country;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.services.CityService;
import com.antonio.skybase.services.CountryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/web/cities")
public class CityMvcController {

    @Autowired
    private CityService cityService;

    @Autowired
    private CountryService countryService;

    // Display list of all cities
    @GetMapping
    public String getAllCities(Model model) {
        List<City> cities = cityService.getAll();
        model.addAttribute("cities", cities);
        return "cities/list";
    }

    // Show form to create a new city
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("cityDTO", new CityDTO());
        List<Country> countries = countryService.getAll();
        model.addAttribute("countries", countries);
        return "cities/form";
    }

    // Handle city creation
    @PostMapping
    public String createCity(@Valid @ModelAttribute("cityDTO") CityDTO cityDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            List<Country> countries = countryService.getAll();
            model.addAttribute("countries", countries);
            return "cities/form";
        }

        try {
            cityService.create(cityDTO);
            redirectAttributes.addFlashAttribute("successMessage", "City created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating city: " + e.getMessage());
        }

        return "redirect:/web/cities";
    }

    // Show city details
    @GetMapping("/{id}")
    public String getCityById(@PathVariable("id") Integer id, Model model) {
        try {
            City city = cityService.getById(id);
            model.addAttribute("city", city);
            return "cities/details";
        } catch (NotFoundException e) {
            model.addAttribute("errorMessage", "City not found");
            return "redirect:/web/cities";
        }
    }

    // Show form to edit an existing city
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        try {
            City city = cityService.getById(id);
            CityDTO cityDTO = new CityDTO();
            cityDTO.setName(city.getName());
            cityDTO.setCountryId(city.getCountry().getId());

            model.addAttribute("cityDTO", cityDTO);
            model.addAttribute("cityId", id);
            List<Country> countries = countryService.getAll();
            model.addAttribute("countries", countries);
            return "cities/form";
        } catch (NotFoundException e) {
            model.addAttribute("errorMessage", "City not found");
            return "redirect:/web/cities";
        }
    }

    // Handle city update
    @PostMapping("/{id}")
    public String updateCity(@PathVariable("id") Integer id,
            @Valid @ModelAttribute("cityDTO") CityDTO cityDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            List<Country> countries = countryService.getAll();
            model.addAttribute("countries", countries);
            model.addAttribute("cityId", id);
            return "cities/form";
        }

        try {
            cityService.update(id, cityDTO);
            redirectAttributes.addFlashAttribute("successMessage", "City updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating city: " + e.getMessage());
        }

        return "redirect:/web/cities";
    }

    // Handle city deletion
    @PostMapping("/{id}/delete")
    public String deleteCity(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            cityService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "City deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting city: " + e.getMessage());
        }

        return "redirect:/web/cities";
    }
}
