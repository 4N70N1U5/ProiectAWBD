package com.antonio.skybase.services;

import com.antonio.skybase.entities.Department;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.repositories.DepartmentRepository;
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

class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateDepartment() {
        Department department = new Department();
        department.setName("Test Department");

        Department savedDepartment = new Department();
        savedDepartment.setId(1);
        savedDepartment.setName("Test Department");

        when(departmentRepository.save(any(Department.class))).thenReturn(savedDepartment);

        Department createdDepartment = departmentService.create(department);

        assertNotNull(createdDepartment);
        assertEquals(1, createdDepartment.getId());
        assertEquals("Test Department", createdDepartment.getName());
    }

    @Test
    void testGetAllDepartments() {
        Department department1 = new Department();
        department1.setId(1);
        department1.setName("Department 1");

        Department department2 = new Department();
        department2.setId(2);
        department2.setName("Department 2");

        List<Department> departments = Arrays.asList(department1, department2);

        when(departmentRepository.findAll()).thenReturn(departments);

        List<Department> result = departmentService.getAll();

        assertEquals(2, result.size());
        assertEquals("Department 1", result.get(0).getName());
        assertEquals("Department 2", result.get(1).getName());
    }

    @Test
    void testGetDepartmentById() {
        Department department = new Department();
        department.setId(1);
        department.setName("Test Department");

        when(departmentRepository.findById(anyInt())).thenReturn(Optional.of(department));

        Department result = departmentService.getById(1);

        assertNotNull(result);
        assertEquals("Test Department", result.getName());
    }

    @Test
    void testGetDepartmentByIdNotFound() {
        when(departmentRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> departmentService.getById(1));
    }

    @Test
    void testUpdateDepartment() {
        Department existingDepartment = new Department();
        existingDepartment.setId(1);
        existingDepartment.setName("Existing Department");

        Department updatedDepartment = new Department();
        updatedDepartment.setName("Updated Department");

        when(departmentRepository.findById(anyInt())).thenReturn(Optional.of(existingDepartment));
        when(departmentRepository.save(any(Department.class))).thenReturn(updatedDepartment);

        Department result = departmentService.update(1, updatedDepartment);

        assertNotNull(result);
        assertEquals("Updated Department", result.getName());
    }

    @Test
    void testDeleteDepartment() {
        doNothing().when(departmentRepository).deleteById(anyInt());

        departmentService.delete(1);

        verify(departmentRepository, times(1)).deleteById(1);
    }
}