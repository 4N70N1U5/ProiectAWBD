package com.antonio.skybase.controllers;

import com.antonio.skybase.config.TestConfig;
import com.antonio.skybase.entities.Department;
import com.antonio.skybase.repositories.DepartmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(TestConfig.class)
@ActiveProfiles("test")
@Transactional
class DepartmentControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        departmentRepository.deleteAll();
    }

    @Test
    void testCreateDepartmentSuccess() throws Exception {
        // Given
        Department department = new Department();
        department.setName("Integration Test Department");

        // When & Then
        mockMvc.perform(post("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(department)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Integration Test Department"))
                .andExpect(jsonPath("$.id").exists());

        // Verify in database
        assertThat(departmentRepository.count()).isEqualTo(1);
        Department savedDepartment = departmentRepository.findAll().get(0);
        assertThat(savedDepartment.getName()).isEqualTo("Integration Test Department");
    }

    @Test
    void testCreateDepartmentWithBlankName() throws Exception {
        // Given
        Department department = new Department();
        department.setName("");

        // When & Then
        mockMvc.perform(post("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(department)))
                .andExpect(status().isBadRequest());

        // Verify nothing was saved
        assertThat(departmentRepository.count()).isEqualTo(0);
    }

    @Test
    void testGetAllDepartments() throws Exception {
        // Given
        Department dept1 = new Department();
        dept1.setName("Department 1");
        Department dept2 = new Department();
        dept2.setName("Department 2");

        departmentRepository.save(dept1);
        departmentRepository.save(dept2);

        // When & Then
        mockMvc.perform(get("/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Department 1"))
                .andExpect(jsonPath("$[1].name").value("Department 2"));
    }

    @Test
    void testGetDepartmentByIdSuccess() throws Exception {
        // Given
        Department department = new Department();
        department.setName("Test Department");
        Department saved = departmentRepository.save(department);

        // When & Then
        mockMvc.perform(get("/departments/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Test Department"));
    }

    @Test
    void testGetDepartmentByIdNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/departments/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateDepartmentSuccess() throws Exception {
        // Given
        Department department = new Department();
        department.setName("Original Department");
        Department saved = departmentRepository.save(department);

        Department updatedDepartment = new Department();
        updatedDepartment.setName("Updated Department");

        // When & Then
        mockMvc.perform(put("/departments/{id}", saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDepartment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Updated Department"));

        // Verify in database
        Department fromDb = departmentRepository.findById(saved.getId()).orElse(null);
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getName()).isEqualTo("Updated Department");
    }

    @Test
    void testUpdateDepartmentNotFound() throws Exception {
        // Given
        Department department = new Department();
        department.setName("Updated Department");

        // When & Then
        mockMvc.perform(put("/departments/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(department)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteDepartmentSuccess() throws Exception {
        // Given
        Department department = new Department();
        department.setName("To Be Deleted");
        Department saved = departmentRepository.save(department);

        // When & Then
        mockMvc.perform(delete("/departments/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        // Verify deletion
        assertThat(departmentRepository.existsById(saved.getId())).isFalse();
    }

    @Test
    void testDeleteDepartmentNotFound() throws Exception {
        // When & Then
        mockMvc.perform(delete("/departments/999"))
                .andExpect(status().isNoContent()); // Spring's default behavior for delete
    }
}
