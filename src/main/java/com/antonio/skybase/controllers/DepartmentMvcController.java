package com.antonio.skybase.controllers;

import com.antonio.skybase.entities.Department;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.services.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/web/departments")
public class DepartmentMvcController {

    @Autowired
    private DepartmentService departmentService;

    // Display list of all departments
    @GetMapping
    public String getAllDepartments(Model model) {
        List<Department> departments = departmentService.getAll();
        model.addAttribute("departments", departments);
        return "departments/list";
    }

    // Show form to create a new department
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("department", new Department());
        return "departments/form";
    }

    // Handle department creation
    @PostMapping
    public String createDepartment(@Valid @ModelAttribute("department") Department department,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "departments/form";
        }

        try {
            departmentService.create(department);
            redirectAttributes.addFlashAttribute("successMessage", "Department created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating department: " + e.getMessage());
        }

        return "redirect:/web/departments";
    }

    // Show department details
    @GetMapping("/{id}")
    public String getDepartmentById(@PathVariable("id") Integer id, Model model) {
        try {
            Department department = departmentService.getById(id);
            model.addAttribute("department", department);
            return "departments/details";
        } catch (NotFoundException e) {
            model.addAttribute("errorMessage", "Department not found");
            List<Department> departments = departmentService.getAll();
            model.addAttribute("departments", departments);
            return "departments/list";
        }
    }

    // Show form to edit an existing department
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Department department = departmentService.getById(id);
            model.addAttribute("department", department);
            return "departments/form";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Department not found");
            return "redirect:/web/departments";
        }
    }

    // Handle department update
    @PostMapping("/{id}")
    public String updateDepartment(@PathVariable("id") Integer id,
            @Valid @ModelAttribute("department") Department department,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "departments/form";
        }

        try {
            departmentService.update(id, department);
            redirectAttributes.addFlashAttribute("successMessage", "Department updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating department: " + e.getMessage());
        }

        return "redirect:/web/departments";
    }

    // Handle department deletion
    @PostMapping("/{id}/delete")
    public String deleteDepartment(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            departmentService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Department deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting department: " + e.getMessage());
        }

        return "redirect:/web/departments";
    }
}
