package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.EmployeeDTO;
import com.antonio.skybase.entities.Employee;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.services.EmployeeService;
import com.antonio.skybase.services.JobService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/web/employees")
public class EmployeeMvcController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JobService jobService;

    // Display list of all employees
    @GetMapping
    public String getAllEmployees(Model model) {
        List<Employee> employees = employeeService.getAll();
        model.addAttribute("employees", employees);
        return "employees/list";
    }

    // Show form to create a new employee
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("employeeDTO", new EmployeeDTO());
        model.addAttribute("jobs", jobService.getAll());
        model.addAttribute("employees", employeeService.getAll()); // For manager selection
        return "employees/form";
    }

    // Handle employee creation
    @PostMapping
    public String createEmployee(@Valid @ModelAttribute("employeeDTO") EmployeeDTO employeeDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("jobs", jobService.getAll());
            model.addAttribute("employees", employeeService.getAll());
            return "employees/form";
        }

        try {
            employeeService.create(employeeDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Employee created successfully!");
        } catch (Exception e) {
            model.addAttribute("jobs", jobService.getAll());
            model.addAttribute("employees", employeeService.getAll());
            model.addAttribute("errorMessage", "Error creating employee: " + e.getMessage());
            return "employees/form";
        }

        return "redirect:/web/employees";
    }

    // Show employee details
    @GetMapping("/{id}")
    public String getEmployeeById(@PathVariable("id") Integer id, Model model) {
        try {
            Employee employee = employeeService.getById(id);
            model.addAttribute("employee", employee);
            return "employees/details";
        } catch (NotFoundException e) {
            model.addAttribute("errorMessage", "Employee not found");
            model.addAttribute("employees", employeeService.getAll());
            return "employees/list";
        }
    }

    // Show form to edit an existing employee
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        try {
            Employee employee = employeeService.getById(id);

            EmployeeDTO employeeDTO = new EmployeeDTO();
            employeeDTO.setId(employee.getId());
            employeeDTO.setFirstName(employee.getFirstName());
            employeeDTO.setLastName(employee.getLastName());
            employeeDTO.setPhoneNumber(employee.getPhoneNumber());
            employeeDTO.setEmail(employee.getEmail());
            employeeDTO.setSalary(employee.getSalary());
            employeeDTO.setJobId(employee.getJob().getId());
            employeeDTO.setFlightHours(employee.getFlightHours());
            if (employee.getManager() != null) {
                employeeDTO.setManagerId(employee.getManager().getId());
            }

            model.addAttribute("employeeDTO", employeeDTO);
            model.addAttribute("jobs", jobService.getAll());
            model.addAttribute("employees", employeeService.getAll());
            return "employees/form";
        } catch (NotFoundException e) {
            model.addAttribute("errorMessage", "Employee not found");
            return "redirect:/web/employees";
        }
    }

    // Handle employee update
    @PostMapping("/{id}")
    public String updateEmployee(@PathVariable("id") Integer id,
            @Valid @ModelAttribute("employeeDTO") EmployeeDTO employeeDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("jobs", jobService.getAll());
            model.addAttribute("employees", employeeService.getAll());
            return "employees/form";
        }

        try {
            employeeService.update(id, employeeDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Employee updated successfully!");
        } catch (Exception e) {
            model.addAttribute("jobs", jobService.getAll());
            model.addAttribute("employees", employeeService.getAll());
            model.addAttribute("errorMessage", "Error updating employee: " + e.getMessage());
            return "employees/form";
        }

        return "redirect:/web/employees";
    }

    // Handle employee deletion
    @PostMapping("/{id}/delete")
    public String deleteEmployee(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            employeeService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Employee deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting employee: " + e.getMessage());
        }

        return "redirect:/web/employees";
    }
}
