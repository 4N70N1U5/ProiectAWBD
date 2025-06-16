package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.JobDTO;
import com.antonio.skybase.entities.Job;
import com.antonio.skybase.responses.ErrorResponse;
import com.antonio.skybase.services.JobService;
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

@Tag(name = "Job controller")
@RestController
@RequestMapping("/jobs")
public class JobController {
    @Autowired
    private JobService jobService;

    @Operation(summary = "Create a new job", description = "Create a new job")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Job created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Job.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<Job> create(@RequestBody @Valid JobDTO jobDTO) {
        Job createdJob = jobService.create(jobDTO);

        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdJob.getId())
                .toUri()).body(createdJob);
    }

    @Operation(summary = "Get all jobs", description = "Get all jobs")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of jobs",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Job.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<Job>> getAll() {
        return ResponseEntity.ok(jobService.getAll());
    }

    @Operation(summary = "Get a job by id", description = "Get a job by id")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Job found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Job.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Job not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Job> getById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(jobService.getById(id));
    }

    @Operation(summary = "Update a job", description = "Update a job")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Job updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Job.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Job not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Job> update(@PathVariable("id") Integer id, @RequestBody @Valid JobDTO jobDTO) {
        return ResponseEntity.ok(jobService.update(id, jobDTO));
    }

    @Operation(summary = "Delete a job", description = "Delete a job")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Job deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Job not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        jobService.delete(id);
        return ResponseEntity.noContent().build();
    }
}