package com.antonio.skybase.controllers;

import com.antonio.skybase.entities.Department;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.services.DepartmentService;
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
class DepartmentMvcControllerTest {

    @Mock
    private DepartmentService departmentService;

    @InjectMocks
    private DepartmentMvcController departmentMvcController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(departmentMvcController).build();
    }

    @Test
    void getAllDepartments_ShouldReturnListView() throws Exception {
        // Arrange
        Department dept1 = new Department();
        dept1.setId(1);
        dept1.setName("Engineering");

        Department dept2 = new Department();
        dept2.setId(2);
        dept2.setName("Finance");

        List<Department> departments = Arrays.asList(dept1, dept2);
        when(departmentService.getAll()).thenReturn(departments);

        // Act & Assert
        mockMvc.perform(get("/web/departments"))
                .andExpect(status().isOk())
                .andExpect(view().name("departments/list"))
                .andExpect(model().attributeExists("departments"))
                .andExpect(model().attribute("departments", departments));

        verify(departmentService).getAll();
    }

    @Test
    void showCreateForm_ShouldReturnFormView() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/web/departments/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("departments/form"))
                .andExpect(model().attributeExists("department"));

        verifyNoInteractions(departmentService);
    }

    @Test
    void createDepartment_WithValidData_ShouldRedirectToList() throws Exception {
        // Arrange
        Department department = new Department();
        department.setName("New Department");

        // Act & Assert
        mockMvc.perform(post("/web/departments")
                .param("name", "New Department"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/departments"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(departmentService).create(any(Department.class));
    }

    @Test
    void createDepartment_WithInvalidData_ShouldReturnFormView() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/web/departments")
                .param("name", "")) // Empty name should trigger validation error
                .andExpect(status().isOk())
                .andExpect(view().name("departments/form"))
                .andExpect(model().attributeExists("department"))
                .andExpect(model().hasErrors());

        verifyNoInteractions(departmentService);
    }

    @Test
    void createDepartment_ServiceThrowsException_ShouldRedirectWithErrorMessage() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Database error")).when(departmentService).create(any(Department.class));

        // Act & Assert
        mockMvc.perform(post("/web/departments")
                .param("name", "Test Department"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/departments"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(departmentService).create(any(Department.class));
    }

    @Test
    void getDepartmentById_WithValidId_ShouldReturnDetailsView() throws Exception {
        // Arrange
        Department department = new Department();
        department.setId(1);
        department.setName("Engineering");
        when(departmentService.getById(1)).thenReturn(department);

        // Act & Assert
        mockMvc.perform(get("/web/departments/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("departments/details"))
                .andExpect(model().attributeExists("department"))
                .andExpect(model().attribute("department", department));

        verify(departmentService).getById(1);
    }

    @Test
    void getDepartmentById_WithInvalidId_ShouldReturnListViewWithError() throws Exception {
        // Arrange
        when(departmentService.getById(999)).thenThrow(new NotFoundException("Department not found"));

        Department dept1 = new Department();
        dept1.setId(1);
        dept1.setName("Engineering");
        List<Department> departments = Arrays.asList(dept1);
        when(departmentService.getAll()).thenReturn(departments);

        // Act & Assert
        mockMvc.perform(get("/web/departments/999"))
                .andExpect(status().isOk())
                .andExpect(view().name("departments/list"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attributeExists("departments"))
                .andExpect(model().attribute("departments", departments));

        verify(departmentService).getById(999);
        verify(departmentService).getAll();
    }

    @Test
    void showEditForm_WithValidId_ShouldReturnFormView() throws Exception {
        // Arrange
        Department department = new Department();
        department.setId(1);
        department.setName("Engineering");
        when(departmentService.getById(1)).thenReturn(department);

        // Act & Assert
        mockMvc.perform(get("/web/departments/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("departments/form"))
                .andExpect(model().attributeExists("department"))
                .andExpect(model().attribute("department", department));

        verify(departmentService).getById(1);
    }

    @Test
    void showEditForm_WithInvalidId_ShouldRedirectToList() throws Exception {
        // Arrange
        when(departmentService.getById(999)).thenThrow(new NotFoundException("Department not found"));

        // Act & Assert
        mockMvc.perform(get("/web/departments/999/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/departments"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(departmentService).getById(999);
    }

    @Test
    void updateDepartment_WithValidData_ShouldRedirectToList() throws Exception {
        // Arrange
        Department department = new Department();
        department.setId(1);
        department.setName("Updated Department");

        // Act & Assert
        mockMvc.perform(post("/web/departments/1")
                .param("id", "1")
                .param("name", "Updated Department"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/departments"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(departmentService).update(eq(1), any(Department.class));
    }

    @Test
    void updateDepartment_WithInvalidData_ShouldReturnFormView() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/web/departments/1")
                .param("id", "1")
                .param("name", "")) // Empty name should trigger validation error
                .andExpect(status().isOk())
                .andExpect(view().name("departments/form"))
                .andExpect(model().attributeExists("department"))
                .andExpect(model().hasErrors());

        verifyNoInteractions(departmentService);
    }

    @Test
    void updateDepartment_ServiceThrowsException_ShouldRedirectWithErrorMessage() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Update failed")).when(departmentService).update(eq(1), any(Department.class));

        // Act & Assert
        mockMvc.perform(post("/web/departments/1")
                .param("id", "1")
                .param("name", "Updated Department"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/departments"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(departmentService).update(eq(1), any(Department.class));
    }

    @Test
    void deleteDepartment_WithValidId_ShouldRedirectToList() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/web/departments/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/departments"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(departmentService).delete(1);
    }

    @Test
    void deleteDepartment_ServiceThrowsException_ShouldRedirectWithErrorMessage() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Delete failed")).when(departmentService).delete(1);

        // Act & Assert
        mockMvc.perform(post("/web/departments/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/departments"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(departmentService).delete(1);
    }
}
