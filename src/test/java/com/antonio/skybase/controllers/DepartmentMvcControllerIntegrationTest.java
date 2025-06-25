package com.antonio.skybase.controllers;

import com.antonio.skybase.entities.Department;
import com.antonio.skybase.repositories.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DepartmentMvcControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DepartmentRepository departmentRepository;

    @BeforeEach
    void setUp() {
        departmentRepository.deleteAll();
    }

    @Test
    void testShowDepartmentList() throws Exception {
        // When & Then
        mockMvc.perform(get("/web/departments"))
                .andExpect(status().isOk())
                .andExpect(view().name("departments/list"))
                .andExpect(model().attributeExists("departments"));
    }

    @Test
    void testShowAddDepartmentForm() throws Exception {
        // When & Then
        mockMvc.perform(get("/web/departments/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("departments/form"))
                .andExpect(model().attributeExists("department"));
    }

    @Test
    void testCreateDepartmentSuccess() throws Exception {
        // When & Then
        mockMvc.perform(post("/web/departments")
                .param("name", "Test Department"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/departments"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify department was created
        assertThat(departmentRepository.findAll()).hasSize(1);
        assertThat(departmentRepository.findAll().get(0).getName()).isEqualTo("Test Department");
    }

    @Test
    void testCreateDepartmentValidationError() throws Exception {
        // When & Then - Missing required fields
        mockMvc.perform(post("/web/departments")
                .param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("departments/form"))
                .andExpect(model().attributeExists("department"))
                .andExpect(model().hasErrors());

        // Verify no department was created
        assertThat(departmentRepository.findAll()).isEmpty();
    }

    @Test
    void testShowEditDepartmentForm() throws Exception {
        // Given - Create a department
        Department department = new Department();
        department.setName("Test Department");
        Department saved = departmentRepository.save(department);

        // When & Then
        mockMvc.perform(get("/web/departments/{id}/edit", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("departments/form"))
                .andExpect(model().attributeExists("department"));
    }

    @Test
    void testUpdateDepartmentSuccess() throws Exception {
        // Given - Create a department
        Department department = new Department();
        department.setName("Test Department");
        Department saved = departmentRepository.save(department);

        // When & Then
        mockMvc.perform(post("/web/departments/{id}", saved.getId())
                .param("name", "Updated Department"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/departments"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify department was updated
        Department updatedDepartment = departmentRepository.findById(saved.getId()).orElseThrow();
        assertThat(updatedDepartment.getName()).isEqualTo("Updated Department");
    }

    @Test
    void testDeleteDepartmentSuccess() throws Exception {
        // Given - Create a department
        Department department = new Department();
        department.setName("Test Department");
        Department saved = departmentRepository.save(department);

        // When & Then
        mockMvc.perform(post("/web/departments/{id}/delete", saved.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/departments"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify department was deleted
        assertThat(departmentRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testShowDepartmentNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/web/departments/{id}", 9999))
                .andExpect(status().isOk())
                .andExpect(view().name("departments/list"))
                .andExpect(model().attributeExists("departments"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void testEditDepartmentNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/web/departments/{id}/edit", 9999))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/departments"));
    }

    @Test
    void testDeleteDepartmentNotFound() throws Exception {
        // When & Then - Should still redirect with success (idempotent delete)
        mockMvc.perform(post("/web/departments/{id}/delete", 9999))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/departments"))
                .andExpect(flash().attributeExists("successMessage"));
    }
}
