package com.antonio.skybase.services;

import com.antonio.skybase.dtos.JobDTO;
import com.antonio.skybase.entities.Department;
import com.antonio.skybase.entities.Job;
import com.antonio.skybase.exceptions.BadRequestException;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.repositories.DepartmentRepository;
import com.antonio.skybase.repositories.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private JobService jobService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateJob() {
        JobDTO jobDTO = new JobDTO();
        jobDTO.setTitle("Test Job");
        jobDTO.setMinSalary(50000.0);
        jobDTO.setMaxSalary(100000.0);
        jobDTO.setDepartmentId(1);

        Department department = new Department();
        department.setId(1);
        department.setName("Test Department");

        Job job = new Job();
        job.setId(1);
        job.setTitle("Test Job");
        job.setMinSalary(50000.0);
        job.setMaxSalary(100000.0);
        job.setDepartment(department);

        when(departmentRepository.findById(anyInt())).thenReturn(Optional.of(department));
        when(jobRepository.save(any(Job.class))).thenReturn(job);

        Job createdJob = jobService.create(jobDTO);

        assertNotNull(createdJob);
        assertEquals("Test Job", createdJob.getTitle());
        assertEquals(50000.0, createdJob.getMinSalary());
        assertEquals(100000.0, createdJob.getMaxSalary());
        assertEquals(department, createdJob.getDepartment());
    }

    @Test
    void testCreateJobWithInvalidDepartment() {
        JobDTO jobDTO = new JobDTO();
        jobDTO.setTitle("Test Job");
        jobDTO.setMinSalary(50000.0);
        jobDTO.setMaxSalary(100000.0);
        jobDTO.setDepartmentId(1);

        when(departmentRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> jobService.create(jobDTO));
    }

    @Test
    void testGetAllJobs() {
        Department department = new Department();
        department.setId(1);
        department.setName("Test Department");

        Job job1 = new Job();
        job1.setId(1);
        job1.setTitle("Job 1");
        job1.setMinSalary(50000.0);
        job1.setMaxSalary(100000.0);
        job1.setDepartment(department);

        Job job2 = new Job();
        job2.setId(2);
        job2.setTitle("Job 2");
        job2.setMinSalary(60000.0);
        job2.setMaxSalary(120000.0);
        job2.setDepartment(department);

        List<Job> jobs = Arrays.asList(job1, job2);

        when(jobRepository.findAll()).thenReturn(jobs);

        List<Job> result = jobService.getAll();

        assertEquals(2, result.size());
        assertEquals("Job 1", result.get(0).getTitle());
        assertEquals("Job 2", result.get(1).getTitle());
    }

    @Test
    void testGetJobById() {
        Department department = new Department();
        department.setId(1);
        department.setName("Test Department");

        Job job = new Job();
        job.setId(1);
        job.setTitle("Test Job");
        job.setMinSalary(50000.0);
        job.setMaxSalary(100000.0);
        job.setDepartment(department);

        when(jobRepository.findById(anyInt())).thenReturn(Optional.of(job));

        Job result = jobService.getById(1);

        assertNotNull(result);
        assertEquals("Test Job", result.getTitle());
        assertEquals(50000.0, result.getMinSalary());
        assertEquals(100000.0, result.getMaxSalary());
        assertEquals(department, result.getDepartment());
    }

    @Test
    void testGetJobByIdNotFound() {
        when(jobRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> jobService.getById(1));
    }

    @Test
    void testUpdateJob() {
        Department department = new Department();
        department.setId(1);
        department.setName("Test Department");

        Job job = new Job();
        job.setId(1);
        job.setTitle("Test Job");
        job.setMinSalary(50000.0);
        job.setMaxSalary(100000.0);
        job.setDepartment(department);

        JobDTO jobDTO = new JobDTO();
        jobDTO.setTitle("Updated Job");
        jobDTO.setMinSalary(60000.0);
        jobDTO.setMaxSalary(120000.0);
        jobDTO.setDepartmentId(1);

        when(jobRepository.findById(anyInt())).thenReturn(Optional.of(job));
        when(departmentRepository.findById(anyInt())).thenReturn(Optional.of(department));
        when(jobRepository.save(any(Job.class))).thenReturn(job);

        Job updatedJob = jobService.update(1, jobDTO);

        assertNotNull(updatedJob);
        assertEquals("Updated Job", updatedJob.getTitle());
        assertEquals(60000.0, updatedJob.getMinSalary());
        assertEquals(120000.0, updatedJob.getMaxSalary());
        assertEquals(department, updatedJob.getDepartment());
    }

    @Test
    void testUpdateJobWithInvalidDepartment() {
        Job job = new Job();
        job.setId(1);
        job.setTitle("Test Job");
        job.setMinSalary(50000.0);
        job.setMaxSalary(100000.0);

        JobDTO jobDTO = new JobDTO();
        jobDTO.setTitle("Updated Job");
        jobDTO.setMinSalary(60000.0);
        jobDTO.setMaxSalary(120000.0);
        jobDTO.setDepartmentId(1);

        when(jobRepository.findById(anyInt())).thenReturn(Optional.of(job));
        when(departmentRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> jobService.update(1, jobDTO));
    }

    @Test
    void testDeleteJob() {
        doNothing().when(jobRepository).deleteById(anyInt());

        jobService.delete(1);

        verify(jobRepository, times(1)).deleteById(1);
    }
}