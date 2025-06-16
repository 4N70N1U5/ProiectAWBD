package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.EmployeeDTO;
import com.antonio.skybase.entities.Employee;
import com.antonio.skybase.responses.ErrorResponse;
import com.antonio.skybase.services.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Employee controller")
@RestController
@RequestMapping("/employees")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @Operation(summary = "Create a new employee", description = "Create a new employee")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Employee created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<Employee> create(@RequestBody @Valid EmployeeDTO employeeDTO) {
        Employee createdEmployee = employeeService.create(employeeDTO);

        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdEmployee.getId())
                .toUri()).body(createdEmployee);
    }

    @Operation(summary = "Get all employees", description = "Get all employees")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of employees",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<Employee>> getAll() {
        return ResponseEntity.ok(employeeService.getAll());
    }

    @Operation(summary = "Get an employee by id", description = "Get an employee by id")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Employee found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Employee not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(employeeService.getById(id));
    }

    @Operation(summary = "Get available employees by date", description = "Get available employees by date")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of available employees",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/available")
    public ResponseEntity<List<Employee>> getAvailableEmployees(@RequestParam("date") LocalDate date) {
        return ResponseEntity.ok(employeeService.getAvailableEmployeesByDate(date));
    }

    @Operation(summary = "Get employee availabilities by ID and date range", description = "Get employee availabilities by ID and date range")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of dates representing employee availabilities",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LocalDate.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Employee not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}/availabilities")
    public ResponseEntity<List<LocalDate>> getEmployeeAvailabilities(@PathVariable("id") Integer id, @RequestParam("startDate") LocalDate startDate, @RequestParam("endDate") LocalDate endDate) {
        return ResponseEntity.ok(employeeService.getEmployeeAvailabilities(id, startDate, endDate));
    }

    @Operation(summary = "Update an employee", description = "Update an employee")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Employee updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Employee not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Employee> update(@PathVariable("id") Integer id, @RequestBody @Valid EmployeeDTO employeeDTO) {
        return ResponseEntity.ok(employeeService.update(id, employeeDTO));
    }

    @Operation(summary = "Delete an employee", description = "Delete an employee")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Employee deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Employee not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}