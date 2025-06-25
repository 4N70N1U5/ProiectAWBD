package com.antonio.skybase.controllers;

import com.antonio.skybase.entities.Job;
import com.antonio.skybase.entities.Department;
import com.antonio.skybase.repositories.JobRepository;
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
class JobMvcControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department department;

    @BeforeEach
    void setUp() {
        jobRepository.deleteAll();
        departmentRepository.deleteAll();

        // Create test data
        department = new Department();
        department.setName("Test Department");
        departmentRepository.save(department);
    }

    @Test
    void testShowJobList() throws Exception {
        // When & Then
        mockMvc.perform(get("/web/jobs"))
                .andExpect(status().isOk())
                .andExpect(view().name("jobs/list"))
                .andExpect(model().attributeExists("jobs"));
    }

    @Test
    void testShowAddJobForm() throws Exception {
        // When & Then
        mockMvc.perform(get("/web/jobs/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("jobs/form"))
                .andExpect(model().attributeExists("jobDTO"))
                .andExpect(model().attributeExists("departments"));
    }

    @Test
    void testCreateJobSuccess() throws Exception {
        // When & Then
        mockMvc.perform(post("/web/jobs")
                .param("title", "Test Job")
                .param("minSalary", "45000")
                .param("maxSalary", "55000")
                .param("departmentId", department.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/jobs"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify job was created
        assertThat(jobRepository.findAll()).hasSize(1);
        assertThat(jobRepository.findAll().get(0).getTitle()).isEqualTo("Test Job");
    }

    @Test
    void testCreateJobValidationError() throws Exception {
        // When & Then - Missing required fields
        mockMvc.perform(post("/web/jobs")
                .param("title", "")
                .param("minSalary", "")
                .param("maxSalary", "")
                .param("departmentId", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("jobs/form"))
                .andExpect(model().attributeExists("jobDTO"))
                .andExpect(model().attributeExists("departments"))
                .andExpect(model().hasErrors());

        // Verify no job was created
        assertThat(jobRepository.findAll()).isEmpty();
    }

    @Test
    void testShowEditJobForm() throws Exception {
        // Given - Create a job
        Job job = new Job();
        job.setTitle("Test Job");
        job.setMinSalary(45000.0);
        job.setMaxSalary(55000.0);
        job.setDepartment(department);
        Job saved = jobRepository.save(job);

        // When & Then
        mockMvc.perform(get("/web/jobs/{id}/edit", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("jobs/form"))
                .andExpect(model().attributeExists("jobDTO"))
                .andExpect(model().attributeExists("departments"))
                .andExpect(model().attributeExists("jobId"));
    }

    @Test
    void testUpdateJobSuccess() throws Exception {
        // Given - Create a job
        Job job = new Job();
        job.setTitle("Test Job");
        job.setMinSalary(45000.0);
        job.setMaxSalary(55000.0);
        job.setDepartment(department);
        Job saved = jobRepository.save(job);

        // When & Then
        mockMvc.perform(post("/web/jobs/{id}", saved.getId())
                .param("title", "Updated Job")
                .param("minSalary", "50000")
                .param("maxSalary", "70000")
                .param("departmentId", department.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/jobs"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify job was updated
        Job updatedJob = jobRepository.findById(saved.getId()).orElseThrow();
        assertThat(updatedJob.getTitle()).isEqualTo("Updated Job");
        assertThat(updatedJob.getMaxSalary()).isEqualTo(70000.0);
    }

    @Test
    void testDeleteJobSuccess() throws Exception {
        // Given - Create a job
        Job job = new Job();
        job.setTitle("Test Job");
        job.setMinSalary(45000.0);
        job.setMaxSalary(55000.0);
        job.setDepartment(department);
        Job saved = jobRepository.save(job);

        // When & Then
        mockMvc.perform(post("/web/jobs/{id}/delete", saved.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/jobs"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify job was deleted
        assertThat(jobRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testShowJobNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/web/jobs/{id}", 9999))
                .andExpect(status().isOk())
                .andExpect(view().name("jobs/list"))
                .andExpect(model().attributeExists("jobs"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void testEditJobNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/web/jobs/{id}/edit", 9999))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/jobs"));
    }

    @Test
    void testDeleteJobNotFound() throws Exception {
        // When & Then - Should still redirect with success (idempotent delete)
        mockMvc.perform(post("/web/jobs/{id}/delete", 9999))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/jobs"))
                .andExpect(flash().attributeExists("successMessage"));
    }
}
