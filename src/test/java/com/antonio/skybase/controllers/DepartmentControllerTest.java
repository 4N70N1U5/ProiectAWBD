package com.antonio.skybase.controllers;

import com.antonio.skybase.entities.Department;
import com.antonio.skybase.services.DepartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DepartmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DepartmentService departmentService;

    @InjectMocks
    private DepartmentController departmentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(departmentController).build();
    }

    @Test
    void testCreateDepartment() throws Exception {
        Department department = new Department();
        department.setId(1);
        department.setName("Test Department");

        when(departmentService.create(any(Department.class))).thenReturn(department);

        mockMvc.perform(post("/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test Department\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Department"));
    }

    @Test
    void testCreateDepartmentWithBlankName() throws Exception {
        mockMvc.perform(post("/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllDepartments() throws Exception {
        Department department1 = new Department();
        department1.setId(1);
        department1.setName("Department 1");

        Department department2 = new Department();
        department2.setId(2);
        department2.setName("Department 2");

        when(departmentService.getAll()).thenReturn(List.of(department1, department2));

        mockMvc.perform(get("/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Department 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Department 2"));
    }

    @Test
    void testGetDepartmentById() throws Exception {
        Department department = new Department();
        department.setId(1);
        department.setName("Test Department");

        when(departmentService.getById(anyInt())).thenReturn(department);

        mockMvc.perform(get("/departments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Department"));
    }

    @Test
    void testUpdateDepartment() throws Exception {
        Department department = new Department();
        department.setId(1);
        department.setName("Updated Department");

        when(departmentService.update(anyInt(), any(Department.class))).thenReturn(department);

        mockMvc.perform(put("/departments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Updated Department\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Department"));
    }

    @Test
    void testDeleteDepartment() throws Exception {
        mockMvc.perform(delete("/departments/1"))
                .andExpect(status().isNoContent());
    }
}