package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.JobDTO;
import com.antonio.skybase.entities.Department;
import com.antonio.skybase.entities.Job;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.services.DepartmentService;
import com.antonio.skybase.services.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class JobMvcControllerTest {

    @Mock
    private JobService jobService;

    @Mock
    private DepartmentService departmentService;

    @InjectMocks
    private JobMvcController jobMvcController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(jobMvcController).build();
    }

    @Test
    void getAllJobs_ShouldReturnListView() throws Exception {
        // Arrange
        Job job1 = new Job();
        job1.setId(1);
        job1.setTitle("Software Developer");

        Job job2 = new Job();
        job2.setId(2);
        job2.setTitle("Project Manager");

        List<Job> jobs = Arrays.asList(job1, job2);
        when(jobService.getAll()).thenReturn(jobs);

        // Act & Assert
        mockMvc.perform(get("/web/jobs"))
                .andExpect(status().isOk())
                .andExpect(view().name("jobs/list"))
                .andExpect(model().attributeExists("jobs"))
                .andExpect(model().attribute("jobs", jobs));

        verify(jobService).getAll();
    }

    @Test
    void showCreateForm_ShouldReturnFormView() throws Exception {
        // Arrange
        Department dept = new Department();
        dept.setId(1);
        dept.setName("Engineering");
        List<Department> departments = Arrays.asList(dept);
        when(departmentService.getAll()).thenReturn(departments);

        // Act & Assert
        mockMvc.perform(get("/web/jobs/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("jobs/form"))
                .andExpect(model().attributeExists("jobDTO"))
                .andExpect(model().attributeExists("departments"))
                .andExpect(model().attribute("departments", departments));

        verify(departmentService).getAll();
    }

    @Test
    void createJob_WithValidData_ShouldRedirectToList() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/web/jobs")
                .param("title", "Software Developer")
                .param("minSalary", "50000")
                .param("maxSalary", "100000")
                .param("departmentId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/jobs"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(jobService).create(any(JobDTO.class));
    }

    @Test
    void createJob_WithInvalidData_ShouldReturnFormView() throws Exception {
        // Arrange
        Department dept = new Department();
        dept.setId(1);
        dept.setName("Engineering");
        List<Department> departments = Arrays.asList(dept);
        when(departmentService.getAll()).thenReturn(departments);

        // Act & Assert
        mockMvc.perform(post("/web/jobs")
                .param("title", "") // Empty title should trigger validation error
                .param("minSalary", "50000")
                .param("maxSalary", "100000")
                .param("departmentId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("jobs/form"))
                .andExpect(model().attributeExists("jobDTO"))
                .andExpect(model().attributeExists("departments"))
                .andExpect(model().hasErrors());

        verify(departmentService).getAll();
        verifyNoInteractions(jobService);
    }

    @Test
    void createJob_ServiceThrowsException_ShouldReturnFormWithErrorMessage() throws Exception {
        // Arrange
        Department dept = new Department();
        dept.setId(1);
        dept.setName("Engineering");
        List<Department> departments = Arrays.asList(dept);
        when(departmentService.getAll()).thenReturn(departments);
        doThrow(new RuntimeException("Database error")).when(jobService).create(any(JobDTO.class));

        // Act & Assert
        mockMvc.perform(post("/web/jobs")
                .param("title", "Software Developer")
                .param("minSalary", "50000")
                .param("maxSalary", "100000")
                .param("departmentId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("jobs/form"))
                .andExpect(model().attributeExists("jobDTO"))
                .andExpect(model().attributeExists("departments"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(jobService).create(any(JobDTO.class));
        verify(departmentService).getAll();
    }

    @Test
    void getJobById_WithValidId_ShouldReturnDetailsView() throws Exception {
        // Arrange
        Department dept = new Department();
        dept.setId(1);
        dept.setName("Engineering");

        Job job = new Job();
        job.setId(1);
        job.setTitle("Software Developer");
        job.setDepartment(dept);
        when(jobService.getById(1)).thenReturn(job);

        // Act & Assert
        mockMvc.perform(get("/web/jobs/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("jobs/details"))
                .andExpect(model().attributeExists("job"))
                .andExpect(model().attribute("job", job));

        verify(jobService).getById(1);
    }

    @Test
    void getJobById_WithInvalidId_ShouldReturnListViewWithError() throws Exception {
        // Arrange
        when(jobService.getById(999)).thenThrow(new NotFoundException("Job not found"));

        Job job1 = new Job();
        job1.setId(1);
        job1.setTitle("Software Developer");
        List<Job> jobs = Arrays.asList(job1);
        when(jobService.getAll()).thenReturn(jobs);

        // Act & Assert
        mockMvc.perform(get("/web/jobs/999"))
                .andExpect(status().isOk())
                .andExpect(view().name("jobs/list"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attributeExists("jobs"))
                .andExpect(model().attribute("jobs", jobs));

        verify(jobService).getById(999);
        verify(jobService).getAll();
    }

    @Test
    void showEditForm_WithValidId_ShouldReturnFormView() throws Exception {
        // Arrange
        Department dept = new Department();
        dept.setId(1);
        dept.setName("Engineering");

        Job job = new Job();
        job.setId(1);
        job.setTitle("Software Developer");
        job.setMinSalary(50000.0);
        job.setMaxSalary(100000.0);
        job.setDepartment(dept);

        List<Department> departments = Arrays.asList(dept);

        when(jobService.getById(1)).thenReturn(job);
        when(departmentService.getAll()).thenReturn(departments);

        // Act & Assert
        mockMvc.perform(get("/web/jobs/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("jobs/form"))
                .andExpect(model().attributeExists("jobDTO"))
                .andExpect(model().attributeExists("departments"))
                .andExpect(model().attribute("departments", departments));

        verify(jobService).getById(1);
        verify(departmentService).getAll();
    }

    @Test
    void showEditForm_WithInvalidId_ShouldRedirectToList() throws Exception {
        // Arrange
        when(jobService.getById(999)).thenThrow(new NotFoundException("Job not found"));

        // Act & Assert
        mockMvc.perform(get("/web/jobs/999/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/jobs"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(jobService).getById(999);
        verifyNoInteractions(departmentService);
    }

    @Test
    void updateJob_WithValidData_ShouldRedirectToList() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/web/jobs/1")
                .param("id", "1")
                .param("title", "Senior Software Developer")
                .param("minSalary", "60000")
                .param("maxSalary", "120000")
                .param("departmentId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/jobs"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(jobService).update(eq(1), any(JobDTO.class));
    }

    @Test
    void updateJob_WithInvalidData_ShouldReturnFormView() throws Exception {
        // Arrange
        Department dept = new Department();
        dept.setId(1);
        dept.setName("Engineering");
        List<Department> departments = Arrays.asList(dept);
        when(departmentService.getAll()).thenReturn(departments);

        // Act & Assert
        mockMvc.perform(post("/web/jobs/1")
                .param("id", "1")
                .param("title", "") // Empty title should trigger validation error
                .param("minSalary", "60000")
                .param("maxSalary", "120000")
                .param("departmentId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("jobs/form"))
                .andExpect(model().attributeExists("jobDTO"))
                .andExpect(model().attributeExists("departments"))
                .andExpect(model().hasErrors());

        verify(departmentService).getAll();
        verifyNoInteractions(jobService);
    }

    @Test
    void updateJob_ServiceThrowsException_ShouldReturnFormWithErrorMessage() throws Exception {
        // Arrange
        Department dept = new Department();
        dept.setId(1);
        dept.setName("Engineering");
        List<Department> departments = Arrays.asList(dept);
        when(departmentService.getAll()).thenReturn(departments);
        doThrow(new RuntimeException("Update failed")).when(jobService).update(eq(1), any(JobDTO.class));

        // Act & Assert
        mockMvc.perform(post("/web/jobs/1")
                .param("id", "1")
                .param("title", "Senior Software Developer")
                .param("minSalary", "60000")
                .param("maxSalary", "120000")
                .param("departmentId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("jobs/form"))
                .andExpect(model().attributeExists("jobDTO"))
                .andExpect(model().attributeExists("departments"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(jobService).update(eq(1), any(JobDTO.class));
        verify(departmentService).getAll();
    }

    @Test
    void deleteJob_WithValidId_ShouldRedirectToList() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/web/jobs/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/jobs"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(jobService).delete(1);
    }

    @Test
    void deleteJob_ServiceThrowsException_ShouldRedirectWithErrorMessage() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Delete failed")).when(jobService).delete(1);

        // Act & Assert
        mockMvc.perform(post("/web/jobs/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/jobs"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(jobService).delete(1);
    }
}
