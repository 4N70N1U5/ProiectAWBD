package com.antonio.skybase.services;

import com.antonio.skybase.dtos.JobDTO;
import com.antonio.skybase.entities.Department;
import com.antonio.skybase.entities.Job;
import com.antonio.skybase.exceptions.BadRequestException;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.repositories.DepartmentRepository;
import com.antonio.skybase.repositories.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    public Job create(JobDTO jobDTO) {
        Department department = validateDepartmentExists(jobDTO.getDepartmentId());
        Job job = new Job();
        job.setTitle(jobDTO.getTitle());
        job.setMinSalary(jobDTO.getMinSalary());
        job.setMaxSalary(jobDTO.getMaxSalary());
        job.setDepartment(department);
        return jobRepository.save(job);
    }

    public List<Job> getAll() {
        return jobRepository.findAll();
    }

    public Job getById(Integer id) {
        return jobRepository.findById(id).orElseThrow(() -> new NotFoundException("Job with ID " + id + " not found"));
    }

    public Job update(Integer id, JobDTO jobDTO) {
        Job jobToUpdate = jobRepository.findById(id).orElseThrow(() -> new NotFoundException("Job with ID " + id + " not found"));
        Department department = validateDepartmentExists(jobDTO.getDepartmentId());
        jobToUpdate.setTitle(jobDTO.getTitle());
        jobToUpdate.setMinSalary(jobDTO.getMinSalary());
        jobToUpdate.setMaxSalary(jobDTO.getMaxSalary());
        jobToUpdate.setDepartment(department);
        return jobRepository.save(jobToUpdate);
    }

    public void delete(Integer id) {
        jobRepository.deleteById(id);
    }

    private Department validateDepartmentExists(Integer departmentId) {
        return departmentRepository.findById(departmentId).orElseThrow(() -> new BadRequestException("Department with ID " + departmentId + " does not exist"));
    }
}