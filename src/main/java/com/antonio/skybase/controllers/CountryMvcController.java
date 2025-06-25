package com.antonio.skybase.controllers;

import com.antonio.skybase.entities.Country;
import com.antonio.skybase.exceptions.NotFoundException;
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
@RequestMapping("/web/countries")
public class CountryMvcController {

    @Autowired
    private CountryService countryService;

    // Display list of all countries
    @GetMapping
    public String getAllCountries(Model model) {
        List<Country> countries = countryService.getAll();
        model.addAttribute("countries", countries);
        return "countries/list";
    }

    // Show form to create a new country
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("country", new Country());
        return "countries/form";
    }

    // Handle country creation
    @PostMapping
    public String createCountry(@Valid @ModelAttribute("country") Country country,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "countries/form";
        }

        try {
            countryService.create(country);
            redirectAttributes.addFlashAttribute("successMessage", "Country created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating country: " + e.getMessage());
        }

        return "redirect:/web/countries";
    }

    // Show country details
    @GetMapping("/{id}")
    public String getCountryById(@PathVariable("id") Integer id, Model model) {
        try {
            Country country = countryService.getById(id);
            model.addAttribute("country", country);
            return "countries/details";
        } catch (NotFoundException e) {
            model.addAttribute("errorMessage", "Country not found");
            model.addAttribute("countries", countryService.getAll());
            return "countries/list";
        }
    }

    // Show form to edit an existing country
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Country country = countryService.getById(id);
            model.addAttribute("country", country);
            return "countries/form";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Country not found");
            return "redirect:/web/countries";
        }
    }

    // Handle country update
    @PostMapping("/{id}")
    public String updateCountry(@PathVariable("id") Integer id,
            @Valid @ModelAttribute("country") Country country,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "countries/form";
        }

        try {
            countryService.update(id, country);
            redirectAttributes.addFlashAttribute("successMessage", "Country updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating country: " + e.getMessage());
        }

        return "redirect:/web/countries";
    }

    // Handle country deletion
    @PostMapping("/{id}/delete")
    public String deleteCountry(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            countryService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Country deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting country: " + e.getMessage());
        }

        return "redirect:/web/countries";
    }
}
