package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.CityDTO;
import com.antonio.skybase.entities.City;
import com.antonio.skybase.responses.ErrorResponse;
import com.antonio.skybase.services.CityService;
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

@Tag(name = "City controller")
@RestController
@RequestMapping("/cities")
public class CityController {
    @Autowired
    private CityService cityService;

    @Operation(summary = "Create a new city", description = "Create a new city")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "City created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = City.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<City> create(@RequestBody @Valid CityDTO cityDTO) {
        City createdCity = cityService.create(cityDTO);

        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCity.getId())
                .toUri()).body(createdCity);
    }

    @Operation(summary = "Get all cities", description = "Get all cities")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of cities",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = City.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<City>> getAll() {
        return ResponseEntity.ok(cityService.getAll());
    }

    @Operation(summary = "Get a city by id", description = "Get a city by id")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "City found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = City.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "City not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<City> getById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(cityService.getById(id));
    }

    @Operation(summary = "Update a city", description = "Update a city")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "City updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = City.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "City not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<City> update(@PathVariable("id") Integer id, @RequestBody @Valid CityDTO cityDTO) {
        return ResponseEntity.ok(cityService.update(id, cityDTO));
    }

    @Operation(summary = "Delete a city", description = "Delete a city")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "City deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "City not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        cityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}