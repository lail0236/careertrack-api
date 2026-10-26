
package com.careertrack.api.controller;

import com.careertrack.api.entity.ApplicationStatusHistory;
import com.careertrack.api.entity.JobApplication;
import com.careertrack.api.service.JobApplicationService;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

    private final JobApplicationService service;

    public JobApplicationController(JobApplicationService service) {
        this.service = service;
    }

    private Long authenticatedUserId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobApplication create(
            @Valid @RequestBody CreateJobApplicationRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return service.create(request, authenticatedUserId(jwt));
    }

    @GetMapping
    public List<JobApplication> findAll(
            @AuthenticationPrincipal Jwt jwt) {
        return service.findAll(authenticatedUserId(jwt));
    }

    @GetMapping("/{id}")
    public JobApplication findById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        return service.findById(id, authenticatedUserId(jwt));
    }

    @PutMapping("/{id}")
    public JobApplication update(
            @PathVariable Long id,
            @Valid @RequestBody CreateJobApplicationRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return service.update(id, request, authenticatedUserId(jwt));
    }

    @PatchMapping("/{id}/status")
    public JobApplication updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateApplicationStatusRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return service.updateStatus(
                id, request.status(), authenticatedUserId(jwt));
    }

    @GetMapping("/{id}/history")
    public List<ApplicationStatusHistory> findStatusHistory(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        return service.findStatusHistory(id, authenticatedUserId(jwt));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        service.delete(id, authenticatedUserId(jwt));
    }
}
