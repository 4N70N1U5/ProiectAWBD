package com.antonio.skybase.repositories;

import com.antonio.skybase.entities.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
}