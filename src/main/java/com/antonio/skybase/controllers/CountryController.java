package com.antonio.skybase.controllers;

import com.antonio.skybase.entities.Country;
import com.antonio.skybase.responses.ErrorResponse;
import com.antonio.skybase.services.CountryService;
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

@Tag(name = "Country controller")
@RestController
@RequestMapping("/countries")
public class CountryController {
    @Autowired
    private CountryService countryService;

    @Operation(summary = "Create a new country", description = "Create a new country")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Country created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Country.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<Country> create(@RequestBody @Valid Country country) {
        Country createdCountry = countryService.create(country);

        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCountry.getId())
                .toUri()).body(createdCountry);
    }

    @Operation(summary = "Get all countries", description = "Get all countries")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of countries",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Country.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<Country>> getAll() {
        return ResponseEntity.ok(countryService.getAll());
    }

    @Operation(summary = "Get a country by id", description = "Get a country by id")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Country found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Country.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Country not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Country> getById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(countryService.getById(id));
    }

    @Operation(summary = "Update a country", description = "Update a country")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Country updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Country.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Country not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Country> update(@PathVariable("id") Integer id, @RequestBody Country country) {
        return ResponseEntity.ok(countryService.update(id, country));
    }

    @Operation(summary = "Delete a country", description = "Delete a country")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Country deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Country not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        countryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}