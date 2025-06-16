package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.AircraftAssignmentDTO;
import com.antonio.skybase.entities.AircraftAssignment;
import com.antonio.skybase.entities.AircraftAssignmentId;
import com.antonio.skybase.responses.ErrorResponse;
import com.antonio.skybase.services.AircraftAssignmentService;
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

@Tag(name = "Aircraft Assignments controller")
@RestController
@RequestMapping("/aircraft-assignments")
public class AircraftAssignmentController {
    @Autowired
    private AircraftAssignmentService aircraftAssignmentService;

    @Operation(summary = "Create a new aircraft assignment", description = "Create a new aircraft assignment")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Aircraft assignment created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AircraftAssignment.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<AircraftAssignment> create(@RequestBody @Valid AircraftAssignmentDTO aircraftAssignmentDTO) {
        AircraftAssignment createdAircraftAssignment = aircraftAssignmentService.create(aircraftAssignmentDTO);

        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{aircraftId}/{flightId}/{date}")
                .buildAndExpand(createdAircraftAssignment.getId().getAircraftId(),
                        createdAircraftAssignment.getId().getFlightId(),
                        createdAircraftAssignment.getId().getDate())
                .toUri()).body(createdAircraftAssignment);
    }

    @Operation(summary = "Get all aircraft assignments", description = "Get all aircraft assignments")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of aircraft assignments",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AircraftAssignment.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<AircraftAssignment>> getAll() {
        return ResponseEntity.ok(aircraftAssignmentService.getAll());
    }

    @Operation(summary = "Get an aircraft assignment by ID", description = "Get an aircraft assignment by ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Aircraft assignment found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AircraftAssignment.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aircraft assignment not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{aircraftId}/{flightId}/{date}")
    public ResponseEntity<AircraftAssignment> getById(@PathVariable("aircraftId") Integer aircraftId,
                                                      @PathVariable("flightId") Integer flightId,
                                                      @PathVariable("date") LocalDate date) {
        AircraftAssignmentId id = new AircraftAssignmentId();
        id.setAircraftId(aircraftId);
        id.setFlightId(flightId);
        id.setDate(date);
        return ResponseEntity.ok(aircraftAssignmentService.getById(id));
    }

    @Operation(summary = "Get aircraft assignments by date", description = "Get aircraft assignments by date")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of aircraft assignments",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AircraftAssignment.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aircraft assignments not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/by-date")
    public ResponseEntity<List<AircraftAssignment>> getAssignmentsByDate(@RequestParam("date") LocalDate date) {
        return ResponseEntity.ok(aircraftAssignmentService.getAssignmentsByDate(date));
    }

    @Operation(summary = "Get aircraft assignments by aircraft and date range", description = "Get aircraft assignments by aircraft and date range")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of aircraft assignments",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AircraftAssignment.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aircraft assignments not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/by-aircraft")
    public ResponseEntity<List<AircraftAssignment>> getAssignmentsByAircraftAndDateRange(@RequestParam("aircraftId") Integer aircraftId,
                                                                                         @RequestParam("startDate") LocalDate startDate,
                                                                                         @RequestParam("endDate") LocalDate endDate) {
        return ResponseEntity.ok(aircraftAssignmentService.getAssignmentsByAircraftAndDateRange(aircraftId, startDate, endDate));
    }

    @Operation(summary = "Get aircraft assignments by flight and date range", description = "Get aircraft assignments by flight and date range")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of aircraft assignments",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AircraftAssignment.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aircraft assignments not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/by-flight")
    public ResponseEntity<List<AircraftAssignment>> getAssignmentsByFlightAndDateRange(@RequestParam("flightId") Integer flightId,
                                                                                         @RequestParam("startDate") LocalDate startDate,
                                                                                         @RequestParam("endDate") LocalDate endDate) {
        return ResponseEntity.ok(aircraftAssignmentService.getAssignmentsByFlightAndDateRange(flightId, startDate, endDate));
    }

    @Operation(summary = "Update an aircraft assignment", description = "Update an aircraft assignment")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Aircraft assignment updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AircraftAssignment.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aircraft assignment not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/{aircraftId}/{flightId}/{date}")
    public ResponseEntity<AircraftAssignment> update(@PathVariable("aircraftId") Integer aircraftId,
                                                     @PathVariable("flightId") Integer flightId,
                                                     @PathVariable("date") LocalDate date,
                                                     @RequestBody @Valid AircraftAssignmentDTO aircraftAssignmentDTO) {
        AircraftAssignmentId id = new AircraftAssignmentId();
        id.setAircraftId(aircraftId);
        id.setFlightId(flightId);
        id.setDate(date);
        return ResponseEntity.ok(aircraftAssignmentService.update(id, aircraftAssignmentDTO));
    }

    @Operation(summary = "Delete an aircraft assignment", description = "Delete an aircraft assignment")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Aircraft assignment deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aircraft assignment not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{aircraftId}/{flightId}/{date}")
    public ResponseEntity<Void> delete(@PathVariable("aircraftId") Integer aircraftId,
                                       @PathVariable("flightId") Integer flightId,
                                       @PathVariable("date") LocalDate date) {
        AircraftAssignmentId id = new AircraftAssignmentId();
        id.setAircraftId(aircraftId);
        id.setFlightId(flightId);
        id.setDate(date);
        aircraftAssignmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}