package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.FlightDTO;
import com.antonio.skybase.entities.Flight;
import com.antonio.skybase.responses.ErrorResponse;
import com.antonio.skybase.services.FlightService;
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

import java.util.List;

@Tag(name = "Flight controller")
@RestController
@RequestMapping("/flights")
public class FlightController {
    @Autowired
    private FlightService flightService;

    @Operation(summary = "Create a new flight", description = "Create a new flight")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Flight created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Flight.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<Flight> create(@RequestBody @Valid FlightDTO flightDTO) {
        Flight createdFlight = flightService.create(flightDTO);

        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdFlight.getId())
                .toUri()).body(createdFlight);
    }

    @Operation(summary = "Get all flights", description = "Get all flights")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of flights",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Flight.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<Flight>> getAll() {
        return ResponseEntity.ok(flightService.getAll());
    }

    @Operation(summary = "Get a flight by id", description = "Get a flight by id")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Flight found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Flight.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Flight not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Flight> getById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(flightService.getById(id));
    }

    @Operation(summary = "Update a flight", description = "Update a flight")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Flight updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Flight.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Flight not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Flight> update(@PathVariable("id") Integer id, @RequestBody @Valid FlightDTO flightDTO) {
        return ResponseEntity.ok(flightService.update(id, flightDTO));
    }

    @Operation(summary = "Delete a flight", description = "Delete a flight")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Flight deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Flight not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        flightService.delete(id);
        return ResponseEntity.noContent().build();
    }
}