
package com.careertrack.api.service;

import com.careertrack.api.controller.CreateJobApplicationRequest;
import com.careertrack.api.entity.JobApplication;
import com.careertrack.api.repository.JobApplicationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class JobApplicationService {

    private final JobApplicationRepository repository;

    public JobApplicationService(JobApplicationRepository repository) {
        this.repository = repository;
    }

    public JobApplication create(CreateJobApplicationRequest request) {
        JobApplication application =
                new JobApplication(request.company().trim(),
                                  request.position().trim());

        application.setLocation(request.location());
        application.setJobUrl(request.jobUrl());
        application.setDateApplied(request.dateApplied());
        application.setNotes(request.notes());

        return repository.save(application);
    }

    @Transactional(readOnly = true)
    public List<JobApplication> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public JobApplication findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Job application not found: " + id
                ));
    }

    public JobApplication update(
            Long id, CreateJobApplicationRequest request) {

        JobApplication application = findById(id);

        application.setCompany(request.company().trim());
        application.setPosition(request.position().trim());
        application.setLocation(request.location());
        application.setJobUrl(request.jobUrl());
        application.setDateApplied(request.dateApplied());
        application.setNotes(request.notes());

        return repository.save(application);
    }

    public void delete(Long id) {
        JobApplication application = findById(id);
        repository.delete(application);
    }
}
