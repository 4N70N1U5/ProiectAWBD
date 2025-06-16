package com.antonio.skybase.services;

import com.antonio.skybase.entities.Department;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.repositories.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    public Department create(Department department) {
        return departmentRepository.save(department);
    }

    public List<Department> getAll() {
        return departmentRepository.findAll();
    }

    public Department getById(Integer id) {
        return departmentRepository.findById(id).orElseThrow(() -> new NotFoundException("Department with ID " + id + " not found"));
    }

    public Department update(Integer id, Department department) {
        Department departmentToUpdate = departmentRepository.findById(id).orElseThrow(() -> new NotFoundException("Department with ID " + id + " not found"));
        departmentToUpdate.setName(department.getName());
        return departmentRepository.save(departmentToUpdate);
    }

    public void delete(Integer id) {
        departmentRepository.deleteById(id);
    }
}