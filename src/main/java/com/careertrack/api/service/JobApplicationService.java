
package com.careertrack.api.service;

import com.careertrack.api.controller.CreateJobApplicationRequest;
import com.careertrack.api.entity.ApplicationStatusHistory;
import com.careertrack.api.entity.JobApplication;
import com.careertrack.api.repository.ApplicationStatusHistoryRepository;
import com.careertrack.api.repository.JobApplicationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional
public class JobApplicationService {

    private static final Set<String> VALID_STATUSES = Set.of(
            "SAVED",
            "APPLIED",
            "ASSESSMENT",
            "INTERVIEW",
            "OFFER",
            "REJECTED"
    );

    private final JobApplicationRepository repository;
    private final ApplicationStatusHistoryRepository historyRepository;

    public JobApplicationService(
            JobApplicationRepository repository,
            ApplicationStatusHistoryRepository historyRepository) {
        this.repository = repository;
        this.historyRepository = historyRepository;
    }

    public JobApplication create(CreateJobApplicationRequest request) {
        JobApplication application =
                new JobApplication(
                        request.company().trim(),
                        request.position().trim()
                );

        application.setLocation(request.location());
        application.setJobUrl(request.jobUrl());
        application.setDateApplied(request.dateApplied());
        application.setNotes(request.notes());

        JobApplication savedApplication = repository.save(application);

        recordStatusChange(
                savedApplication.getId(),
                null,
                savedApplication.getStatus()
        );

        return savedApplication;
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

    public JobApplication updateStatus(Long id, String requestedStatus) {
        JobApplication application = findById(id);

        String normalizedStatus = requestedStatus.trim()
                .toUpperCase(Locale.ROOT);

        if (!VALID_STATUSES.contains(normalizedStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid status '" + requestedStatus
                            + "'. Allowed statuses: "
                            + String.join(
                                    ", ",
                                    VALID_STATUSES.stream().sorted().toList()
                            )
            );
        }

        String previousStatus = application.getStatus();

        if (previousStatus.equals(normalizedStatus)) {
            return application;
        }

        application.setStatus(normalizedStatus);
        JobApplication savedApplication = repository.save(application);

        recordStatusChange(
                savedApplication.getId(),
                previousStatus,
                normalizedStatus
        );

        return savedApplication;
    }

    @Transactional(readOnly = true)
    public List<ApplicationStatusHistory> findStatusHistory(Long id) {
        findById(id);

        return historyRepository
                .findByApplicationIdOrderByChangedAtAscIdAsc(id);
    }

    private void recordStatusChange(
            Long applicationId,
            String previousStatus,
            String newStatus) {

        ApplicationStatusHistory history =
                new ApplicationStatusHistory(
                        applicationId,
                        previousStatus,
                        newStatus
                );

        historyRepository.save(history);
    }

    public void delete(Long id) {
        JobApplication application = findById(id);
        repository.delete(application);
    }
}
