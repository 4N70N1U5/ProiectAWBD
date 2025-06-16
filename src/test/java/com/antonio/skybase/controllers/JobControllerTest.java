package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.JobDTO;
import com.antonio.skybase.entities.Job;
import com.antonio.skybase.services.JobService;
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

class JobControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JobService jobService;

    @InjectMocks
    private JobController jobController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(jobController).build();
    }

    @Test
    void testCreateJob() throws Exception {
        Job job = new Job();
        job.setId(1);
        job.setTitle("Test Job");
        job.setMinSalary(50000.0);
        job.setMaxSalary(100000.0);

        when(jobService.create(any(JobDTO.class))).thenReturn(job);

        mockMvc.perform(post("/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Test Job\", \"minSalary\": 50000, \"maxSalary\": 100000, \"departmentId\": 1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Job"))
                .andExpect(jsonPath("$.minSalary").value(50000.0))
                .andExpect(jsonPath("$.maxSalary").value(100000.0));
    }

    @Test
    void testCreateJobWithBlankTitle() throws Exception {
        mockMvc.perform(post("/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"\", \"minSalary\": 50000, \"maxSalary\": 100000, \"departmentId\": 1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateJobWithNegativeMinSalary() throws Exception {
        mockMvc.perform(post("/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Test Job\", \"minSalary\": -50000, \"maxSalary\": 100000, \"departmentId\": 1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateJobWithNegativeMaxSalary() throws Exception {
        mockMvc.perform(post("/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Test Job\", \"minSalary\": 50000, \"maxSalary\": -100000, \"departmentId\": 1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateJobWithNullDepartmentId() throws Exception {
        mockMvc.perform(post("/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Test Job\", \"minSalary\": 50000, \"maxSalary\": 100000, \"departmentId\": null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllJobs() throws Exception {
        Job job1 = new Job();
        job1.setId(1);
        job1.setTitle("Job 1");
        job1.setMinSalary(50000.0);
        job1.setMaxSalary(100000.0);

        Job job2 = new Job();
        job2.setId(2);
        job2.setTitle("Job 2");
        job2.setMinSalary(60000.0);
        job2.setMaxSalary(120000.0);

        when(jobService.getAll()).thenReturn(List.of(job1, job2));

        mockMvc.perform(get("/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Job 1"))
                .andExpect(jsonPath("$[0].minSalary").value(50000.0))
                .andExpect(jsonPath("$[0].maxSalary").value(100000.0))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Job 2"))
                .andExpect(jsonPath("$[1].minSalary").value(60000.0))
                .andExpect(jsonPath("$[1].maxSalary").value(120000.0));
    }

    @Test
    void testGetJobById() throws Exception {
        Job job = new Job();
        job.setId(1);
        job.setTitle("Test Job");
        job.setMinSalary(50000.0);
        job.setMaxSalary(100000.0);

        when(jobService.getById(anyInt())).thenReturn(job);

        mockMvc.perform(get("/jobs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Job"))
                .andExpect(jsonPath("$.minSalary").value(50000.0))
                .andExpect(jsonPath("$.maxSalary").value(100000.0));
    }

    @Test
    void testUpdateJob() throws Exception {
        Job job = new Job();
        job.setId(1);
        job.setTitle("Updated Job");
        job.setMinSalary(60000.0);
        job.setMaxSalary(120000.0);

        when(jobService.update(anyInt(), any(JobDTO.class))).thenReturn(job);

        mockMvc.perform(put("/jobs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Updated Job\", \"minSalary\": 60000, \"maxSalary\": 120000, \"departmentId\": 1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Job"))
                .andExpect(jsonPath("$.minSalary").value(60000.0))
                .andExpect(jsonPath("$.maxSalary").value(120000.0));
    }

    @Test
    void testDeleteJob() throws Exception {
        mockMvc.perform(delete("/jobs/1"))
                .andExpect(status().isNoContent());
    }
}