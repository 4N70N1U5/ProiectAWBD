package com.antonio.skybase.controllers;

import com.antonio.skybase.entities.Aircraft;
import com.antonio.skybase.responses.ErrorResponse;
import com.antonio.skybase.services.AircraftService;
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

@Tag(name = "Aircraft controller")
@RestController
@RequestMapping("/aircraft")
public class AircraftController {
    @Autowired
    private AircraftService aircraftService;

    @Operation(summary = "Create a new aircraft", description = "Create a new aircraft")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Aircraft created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Aircraft.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<Aircraft> create(@RequestBody @Valid Aircraft aircraft) {
        Aircraft createdAircraft = aircraftService.create(aircraft);

        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdAircraft.getId())
                .toUri()).body(createdAircraft);
    }

    @Operation(summary = "Get all aircraft", description = "Get all aircraft")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of aircraft",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Aircraft.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<Aircraft>> getAll() {
        return ResponseEntity.ok(aircraftService.getAll());
    }

    @Operation(summary = "Get an aircraft by id", description = "Get an aircraft by id")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Aircraft found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Aircraft.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aircraft not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Aircraft> getById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(aircraftService.getById(id));
    }

    @Operation(summary = "Get available aircraft by date", description = "Get available aircraft by date")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of available aircraft",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Aircraft.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/available")
    public ResponseEntity<List<Aircraft>> getAvailableAircraft(@RequestParam("date") LocalDate date) {
        return ResponseEntity.ok(aircraftService.getAvailableAircraftByDate(date));
    }

    @Operation(summary = "Get aircraft availabilities by ID and date range", description = "Get aircraft availabilities by ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of dates representing aircraft availabilities",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LocalDate.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aircraft not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}/availabilities")
    public ResponseEntity<List<LocalDate>> getAircraftAvailabilities(@PathVariable("id") Integer id, @RequestParam("startDate") LocalDate startDate, @RequestParam("endDate") LocalDate endDate) {
        return ResponseEntity.ok(aircraftService.getAircraftAvailabilities(id, startDate, endDate));
    }

    @Operation(summary = "Update an aircraft", description = "Update an aircraft")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Aircraft updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Aircraft.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aircraft not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Aircraft> update(@PathVariable("id") Integer id, @RequestBody @Valid Aircraft aircraft) {
        return ResponseEntity.ok(aircraftService.update(id, aircraft));
    }

    @Operation(summary = "Delete an aircraft", description = "Delete an aircraft")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Aircraft deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aircraft not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        aircraftService.delete(id);
        return ResponseEntity.noContent().build();
    }
}