
package com.careertrack.api.service;

import com.careertrack.api.controller.CreateJobApplicationRequest;
import com.careertrack.api.entity.ApplicationStatusHistory;
import com.careertrack.api.entity.JobApplication;
import com.careertrack.api.entity.User;
import com.careertrack.api.repository.ApplicationStatusHistoryRepository;
import com.careertrack.api.repository.JobApplicationRepository;
import com.careertrack.api.repository.UserRepository;

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
            "SAVED", "APPLIED", "ASSESSMENT",
            "INTERVIEW", "OFFER", "REJECTED"
    );

    private final JobApplicationRepository repository;
    private final ApplicationStatusHistoryRepository historyRepository;
    private final UserRepository userRepository;

    public JobApplicationService(
            JobApplicationRepository repository,
            ApplicationStatusHistoryRepository historyRepository,
            UserRepository userRepository) {
        this.repository = repository;
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
    }

    public JobApplication create(
            CreateJobApplicationRequest request, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User account not found."));

        JobApplication application = new JobApplication(
                request.company().trim(),
                request.position().trim());

        application.setUser(user);
        application.setLocation(request.location());
        application.setJobUrl(request.jobUrl());
        application.setDateApplied(request.dateApplied());
        application.setNotes(request.notes());

        JobApplication saved = repository.save(application);

        recordStatusChange(saved.getId(), null, saved.getStatus());

        return saved;
    }

    @Transactional(readOnly = true)
    public List<JobApplication> findAll(Long userId) {
        return repository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public JobApplication findById(Long id, Long userId) {
        return repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Job application not found."));
    }

    public JobApplication update(
            Long id,
            CreateJobApplicationRequest request,
            Long userId) {

        JobApplication application = findById(id, userId);

        application.setCompany(request.company().trim());
        application.setPosition(request.position().trim());
        application.setLocation(request.location());
        application.setJobUrl(request.jobUrl());
        application.setDateApplied(request.dateApplied());
        application.setNotes(request.notes());

        return repository.save(application);
    }

    public JobApplication updateStatus(
            Long id, String requestedStatus, Long userId) {

        JobApplication application = findById(id, userId);

        String normalizedStatus = requestedStatus.trim()
                .toUpperCase(Locale.ROOT);

        if (!VALID_STATUSES.contains(normalizedStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid status '" + requestedStatus
                            + "'. Allowed statuses: "
                            + String.join(", ",
                                    VALID_STATUSES.stream().sorted().toList())
            );
        }

        String previousStatus = application.getStatus();

        if (previousStatus.equals(normalizedStatus)) {
            return application;
        }

        application.setStatus(normalizedStatus);
        JobApplication saved = repository.save(application);

        recordStatusChange(saved.getId(), previousStatus, normalizedStatus);

        return saved;
    }

    @Transactional(readOnly = true)
    public List<ApplicationStatusHistory> findStatusHistory(
            Long id, Long userId) {

        findById(id, userId);

        return historyRepository
                .findByApplicationIdOrderByChangedAtAscIdAsc(id);
    }

    private void recordStatusChange(
            Long applicationId, String previousStatus, String newStatus) {

        historyRepository.save(new ApplicationStatusHistory(
                applicationId, previousStatus, newStatus));
    }

    public void delete(Long id, Long userId) {
        JobApplication application = findById(id, userId);
        repository.delete(application);
    }
}
