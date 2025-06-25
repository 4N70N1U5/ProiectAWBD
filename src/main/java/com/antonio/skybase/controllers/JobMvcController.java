package com.antonio.skybase.controllers;

import com.antonio.skybase.dtos.JobDTO;
import com.antonio.skybase.entities.Job;
import com.antonio.skybase.exceptions.NotFoundException;
import com.antonio.skybase.services.DepartmentService;
import com.antonio.skybase.services.JobService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/web/jobs")
public class JobMvcController {

    @Autowired
    private JobService jobService;

    @Autowired
    private DepartmentService departmentService;

    // Display list of all jobs
    @GetMapping
    public String getAllJobs(Model model) {
        List<Job> jobs = jobService.getAll();
        model.addAttribute("jobs", jobs);
        return "jobs/list";
    }

    // Show form to create a new job
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("jobDTO", new JobDTO());
        model.addAttribute("departments", departmentService.getAll());
        return "jobs/form";
    }

    // Handle job creation
    @PostMapping
    public String createJob(@Valid @ModelAttribute("jobDTO") JobDTO jobDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", departmentService.getAll());
            return "jobs/form";
        }

        try {
            jobService.create(jobDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Job created successfully!");
        } catch (Exception e) {
            model.addAttribute("departments", departmentService.getAll());
            model.addAttribute("errorMessage", "Error creating job: " + e.getMessage());
            return "jobs/form";
        }

        return "redirect:/web/jobs";
    }

    // Show job details
    @GetMapping("/{id}")
    public String getJobById(@PathVariable("id") Integer id, Model model) {
        try {
            Job job = jobService.getById(id);
            model.addAttribute("job", job);
            return "jobs/details";
        } catch (NotFoundException e) {
            model.addAttribute("errorMessage", "Job not found");
            List<Job> jobs = jobService.getAll();
            model.addAttribute("jobs", jobs);
            return "jobs/list";
        }
    }

    // Show form to edit an existing job
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Job job = jobService.getById(id);

            JobDTO jobDTO = new JobDTO();
            jobDTO.setId(job.getId());
            jobDTO.setTitle(job.getTitle());
            jobDTO.setMinSalary(job.getMinSalary());
            jobDTO.setMaxSalary(job.getMaxSalary());
            jobDTO.setDepartmentId(job.getDepartment().getId());

            model.addAttribute("jobDTO", jobDTO);
            model.addAttribute("jobId", id);
            model.addAttribute("departments", departmentService.getAll());
            return "jobs/form";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Job not found");
            return "redirect:/web/jobs";
        }
    }

    // Handle job update
    @PostMapping("/{id}")
    public String updateJob(@PathVariable("id") Integer id,
            @Valid @ModelAttribute("jobDTO") JobDTO jobDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("jobId", id);
            model.addAttribute("departments", departmentService.getAll());
            return "jobs/form";
        }

        try {
            jobService.update(id, jobDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Job updated successfully!");
        } catch (Exception e) {
            model.addAttribute("jobId", id);
            model.addAttribute("departments", departmentService.getAll());
            model.addAttribute("errorMessage", "Error updating job: " + e.getMessage());
            return "jobs/form";
        }

        return "redirect:/web/jobs";
    }

    // Handle job deletion
    @PostMapping("/{id}/delete")
    public String deleteJob(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            jobService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Job deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting job: " + e.getMessage());
        }

        return "redirect:/web/jobs";
    }
}
