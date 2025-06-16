package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.AirportDTO;
import com.antonio.skybase.entities.Airport;
import com.antonio.skybase.responses.ErrorResponse;
import com.antonio.skybase.services.AirportService;
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

@Tag(name = "Airport controller")
@RestController
@RequestMapping("/airports")
public class AirportController {
    @Autowired
    private AirportService airportService;

    @Operation(summary = "Create a new airport", description = "Create a new airport")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Airport created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Airport.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<Airport> create(@RequestBody @Valid AirportDTO airportDTO) {
        Airport createdAirport = airportService.create(airportDTO);

        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdAirport.getId())
                .toUri()).body(createdAirport);
    }

    @Operation(summary = "Get all airports", description = "Get all airports")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of airports",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Airport.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<Airport>> getAll() {
        return ResponseEntity.ok(airportService.getAll());
    }

    @Operation(summary = "Get an airport by id", description = "Get an airport by id")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Airport found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Airport.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Airport not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Airport> getById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(airportService.getById(id));
    }

    @Operation(summary = "Update an airport", description = "Update an airport")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Airport updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Airport.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Airport not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Airport> update(@PathVariable("id") Integer id, @RequestBody @Valid AirportDTO airportDTO) {
        return ResponseEntity.ok(airportService.update(id, airportDTO));
    }

    @Operation(summary = "Delete an airport", description = "Delete an airport")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Airport deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Airport not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        airportService.delete(id);
        return ResponseEntity.noContent().build();
    }
}