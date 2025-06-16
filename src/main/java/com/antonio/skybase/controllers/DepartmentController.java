package com.antonio.skybase.controllers;

import com.antonio.skybase.entities.Department;
import com.antonio.skybase.responses.ErrorResponse;
import com.antonio.skybase.services.DepartmentService;
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

@Tag(name = "Department controller")
@RestController
@RequestMapping("/departments")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    @Operation(summary = "Create a new department", description = "Create a new department")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Department created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Department.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<Department> create(@RequestBody @Valid Department department) {
        Department createdDepartment = departmentService.create(department);

        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdDepartment.getId())
                .toUri()).body(createdDepartment);
    }

    @Operation(summary = "Get all departments", description = "Get all departments")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of departments",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Department.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<Department>> getAll() {
        return ResponseEntity.ok(departmentService.getAll());
    }

    @Operation(summary = "Get a department by id", description = "Get a department by id")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Department found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Department.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Department not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Department> getById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(departmentService.getById(id));
    }

    @Operation(summary = "Update a department", description = "Update a department")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Department updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Department.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Department not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Department> update(@PathVariable("id") Integer id, @RequestBody @Valid Department department) {
        return ResponseEntity.ok(departmentService.update(id, department));
    }

    @Operation(summary = "Delete a department", description = "Delete a department")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Department deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Department not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        departmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}