package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.EmployeeAssignmentDTO;
import com.antonio.skybase.entities.*;
import com.antonio.skybase.services.EmployeeAssignmentService;
import com.antonio.skybase.services.EmployeeService;
import com.antonio.skybase.services.FlightService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/web/employee-assignments")
public class EmployeeAssignmentMvcController {

    @Autowired
    private EmployeeAssignmentService employeeAssignmentService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private FlightService flightService;

    @GetMapping
    public String list(Model model) {
        List<EmployeeAssignment> employeeAssignments = employeeAssignmentService.getAll();
        model.addAttribute("employeeAssignments", employeeAssignments);
        model.addAttribute("currentPage", "employee-assignments");
        return "employee-assignments/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("employeeAssignmentDTO", new EmployeeAssignmentDTO());
        List<Employee> employees = employeeService.getAll();
        List<Flight> flights = flightService.getAll();
        model.addAttribute("employees", employees);
        model.addAttribute("flights", flights);
        model.addAttribute("currentPage", "employee-assignments");
        return "employee-assignments/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute EmployeeAssignmentDTO employeeAssignmentDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            List<Employee> employees = employeeService.getAll();
            List<Flight> flights = flightService.getAll();
            model.addAttribute("employees", employees);
            model.addAttribute("flights", flights);
            model.addAttribute("currentPage", "employee-assignments");
            return "employee-assignments/form";
        }

        try {
            employeeAssignmentService.create(employeeAssignmentDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Employee assignment created successfully!");
            return "redirect:/web/employee-assignments";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            List<Employee> employees = employeeService.getAll();
            List<Flight> flights = flightService.getAll();
            model.addAttribute("employees", employees);
            model.addAttribute("flights", flights);
            model.addAttribute("currentPage", "employee-assignments");
            return "employee-assignments/form";
        }
    }

    @GetMapping("/{employeeId}/{flightId}/{date}")
    public String details(@PathVariable Integer employeeId,
            @PathVariable Integer flightId,
            @PathVariable String date,
            Model model) {
        EmployeeAssignmentId id = new EmployeeAssignmentId();
        id.setEmployeeId(employeeId);
        id.setFlightId(flightId);
        id.setDate(LocalDate.parse(date));

        EmployeeAssignment employeeAssignment = employeeAssignmentService.getById(id);
        model.addAttribute("employeeAssignment", employeeAssignment);
        model.addAttribute("currentPage", "employee-assignments");
        return "employee-assignments/details";
    }

    @GetMapping("/{employeeId}/{flightId}/{date}/edit")
    public String showEditForm(@PathVariable Integer employeeId,
            @PathVariable Integer flightId,
            @PathVariable String date,
            Model model) {
        EmployeeAssignmentId id = new EmployeeAssignmentId();
        id.setEmployeeId(employeeId);
        id.setFlightId(flightId);
        id.setDate(LocalDate.parse(date));

        EmployeeAssignment employeeAssignment = employeeAssignmentService.getById(id);

        EmployeeAssignmentDTO dto = new EmployeeAssignmentDTO();
        dto.setEmployeeId(employeeAssignment.getId().getEmployeeId());
        dto.setFlightId(employeeAssignment.getId().getFlightId());
        dto.setDate(employeeAssignment.getId().getDate());

        model.addAttribute("employeeAssignmentDTO", dto);
        model.addAttribute("originalId", id);
        List<Employee> employees = employeeService.getAll();
        List<Flight> flights = flightService.getAll();
        model.addAttribute("employees", employees);
        model.addAttribute("flights", flights);
        model.addAttribute("currentPage", "employee-assignments");
        return "employee-assignments/form";
    }

    @PostMapping("/{employeeId}/{flightId}/{date}")
    public String update(@PathVariable Integer employeeId,
            @PathVariable Integer flightId,
            @PathVariable String date,
            @Valid @ModelAttribute EmployeeAssignmentDTO employeeAssignmentDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        EmployeeAssignmentId originalId = new EmployeeAssignmentId();
        originalId.setEmployeeId(employeeId);
        originalId.setFlightId(flightId);
        originalId.setDate(LocalDate.parse(date));

        if (bindingResult.hasErrors()) {
            model.addAttribute("originalId", originalId);
            List<Employee> employees = employeeService.getAll();
            List<Flight> flights = flightService.getAll();
            model.addAttribute("employees", employees);
            model.addAttribute("flights", flights);
            model.addAttribute("currentPage", "employee-assignments");
            return "employee-assignments/form";
        }

        try {
            employeeAssignmentService.update(originalId, employeeAssignmentDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Employee assignment updated successfully!");
            return "redirect:/web/employee-assignments";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("originalId", originalId);
            List<Employee> employees = employeeService.getAll();
            List<Flight> flights = flightService.getAll();
            model.addAttribute("employees", employees);
            model.addAttribute("flights", flights);
            model.addAttribute("currentPage", "employee-assignments");
            return "employee-assignments/form";
        }
    }

    @PostMapping("/{employeeId}/{flightId}/{date}/delete")
    public String delete(@PathVariable Integer employeeId,
            @PathVariable Integer flightId,
            @PathVariable String date,
            RedirectAttributes redirectAttributes) {
        EmployeeAssignmentId id = new EmployeeAssignmentId();
        id.setEmployeeId(employeeId);
        id.setFlightId(flightId);
        id.setDate(LocalDate.parse(date));

        try {
            employeeAssignmentService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Employee assignment deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error deleting employee assignment: " + e.getMessage());
        }
        return "redirect:/web/employee-assignments";
    }
}
