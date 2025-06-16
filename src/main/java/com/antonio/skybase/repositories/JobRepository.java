package com.antonio.skybase.repositories;

import com.antonio.skybase.entities.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Integer> {
}