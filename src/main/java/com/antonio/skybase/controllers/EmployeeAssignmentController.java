package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.EmployeeAssignmentDTO;
import com.antonio.skybase.entities.EmployeeAssignment;
import com.antonio.skybase.entities.EmployeeAssignmentId;
import com.antonio.skybase.responses.ErrorResponse;
import com.antonio.skybase.services.EmployeeAssignmentService;
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

@Tag(name = "Employee Assignments controller")
@RestController
@RequestMapping("/employee-assignments")
public class EmployeeAssignmentController {
    @Autowired
    private EmployeeAssignmentService employeeAssignmentService;

    @Operation(summary = "Create a new employee assignment", description = "Create a new employee assignment")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Employee assignment created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeAssignment.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<EmployeeAssignment> create(@RequestBody @Valid EmployeeAssignmentDTO employeeAssignmentDTO) {
        EmployeeAssignment createdEmployeeAssignment = employeeAssignmentService.create(employeeAssignmentDTO);

        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{employeeId}/{flightId}/{date}")
                .buildAndExpand(createdEmployeeAssignment.getId().getEmployeeId(),
                        createdEmployeeAssignment.getId().getFlightId(),
                        createdEmployeeAssignment.getId().getDate())
                .toUri()).body(createdEmployeeAssignment);
    }

    @Operation(summary = "Get all employee assignments", description = "Get all employee assignments")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of employee assignments",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeAssignment.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<EmployeeAssignment>> getAll() {
        return ResponseEntity.ok(employeeAssignmentService.getAll());
    }

    @Operation(summary = "Get an employee assignment by ID", description = "Get an employee assignment by ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Employee assignment found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeAssignment.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Employee assignment not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{employeeId}/{flightId}/{date}")
    public ResponseEntity<EmployeeAssignment> getById(@PathVariable("employeeId") Integer employeeId,
                                                      @PathVariable("flightId") Integer flightId,
                                                      @PathVariable("date") LocalDate date) {
        EmployeeAssignmentId id = new EmployeeAssignmentId();
        id.setEmployeeId(employeeId);
        id.setFlightId(flightId);
        id.setDate(date);
        return ResponseEntity.ok(employeeAssignmentService.getById(id));
    }

    @Operation(summary = "Get employee assignments by date", description = "Get employee assignments by date")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of employee assignments",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeAssignment.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Employee assignments not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/by-date")
    public ResponseEntity<List<EmployeeAssignment>> getAssignmentsByDate(@RequestParam("date") LocalDate date) {
        return ResponseEntity.ok(employeeAssignmentService.getAssignmentsByDate(date));
    }

    @Operation(summary = "Get employee assignments by employee and date range", description = "Get employee assignments by employee and date range")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of employee assignments",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeAssignment.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Employee assignments not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/by-employee")
    public ResponseEntity<List<EmployeeAssignment>> getAssignmentsByEmployeeAndDateRange(@RequestParam("employeeId") Integer employeeId,
                                                                                         @RequestParam("startDate") LocalDate startDate,
                                                                                         @RequestParam("endDate") LocalDate endDate) {
        return ResponseEntity.ok(employeeAssignmentService.getAssignmentsByEmployeeAndDateRange(employeeId, startDate, endDate));
    }

    @Operation(summary = "Get employee assignments by flight and date range", description = "Get employee assignments by flight and date range")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of employee assignments",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeAssignment.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Employee assignments not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/by-flight")
    public ResponseEntity<List<EmployeeAssignment>> getAssignmentsByFlightAndDateRange(@RequestParam("flightId") Integer flightId,
                                                                                       @RequestParam("startDate") LocalDate startDate,
                                                                                       @RequestParam("endDate") LocalDate endDate) {
        return ResponseEntity.ok(employeeAssignmentService.getAssignmentsByFlightAndDateRange(flightId, startDate, endDate));
    }

    @Operation(summary = "Update an employee assignment", description = "Update an employee assignment")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Employee assignment updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeAssignment.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Employee assignment not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/{employeeId}/{flightId}/{date}")
    public ResponseEntity<EmployeeAssignment> update(@PathVariable("employeeId") Integer employeeId,
                                                     @PathVariable("flightId") Integer flightId,
                                                     @PathVariable("date") LocalDate date,
                                                     @RequestBody @Valid EmployeeAssignmentDTO employeeAssignmentDTO) {
        EmployeeAssignmentId id = new EmployeeAssignmentId();
        id.setEmployeeId(employeeId);
        id.setFlightId(flightId);
        id.setDate(date);
        return ResponseEntity.ok(employeeAssignmentService.update(id, employeeAssignmentDTO));
    }

    @Operation(summary = "Delete an employee assignment", description = "Delete an employee assignment")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Employee assignment deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Employee assignment not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{employeeId}/{flightId}/{date}")
    public ResponseEntity<Void> delete(@PathVariable("employeeId") Integer employeeId,
                                       @PathVariable("flightId") Integer flightId,
                                       @PathVariable("date") LocalDate date) {
        EmployeeAssignmentId id = new EmployeeAssignmentId();
        id.setEmployeeId(employeeId);
        id.setFlightId(flightId);
        id.setDate(date);
        employeeAssignmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}