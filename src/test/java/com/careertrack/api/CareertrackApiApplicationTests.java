
package com.careertrack.api;

import com.careertrack.api.entity.JobApplication;
import com.careertrack.api.entity.User;
import com.careertrack.api.repository.JobApplicationRepository;
import com.careertrack.api.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CareertrackApiApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JobApplicationRepository repository;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(new User(
                "Application Test User",
                "application-" + UUID.randomUUID() + "@example.com",
                "test-password-hash"
        ));
    }

    private RequestPostProcessor authenticatedUser() {
        return jwt().jwt(token ->
                token.subject(testUser.getId().toString()));
    }

    private Map<String, Object> validApplication() {
        return Map.of(
                "company", "Microsoft",
                "position", "Software Engineer Intern",
                "location", "Redmond, WA",
                "jobUrl", "https://careers.microsoft.com/",
                "dateApplied", "2026-10-25",
                "notes", "Automated integration test"
        );
    }

    private JobApplication createOwnedApplication() {
        JobApplication application = new JobApplication(
                "Test Company", "Software Engineer");
        application.setUser(testUser);
        return repository.save(application);
    }

    @Test
    void createApplicationReturnsCreatedRecord() throws Exception {
        mockMvc.perform(post("/api/applications")
                        .with(authenticatedUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                validApplication())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.company").value("Microsoft"))
                .andExpect(jsonPath("$.status").value("SAVED"));
    }

    @Test
    void listApplicationsReturnsArray() throws Exception {
        mockMvc.perform(get("/api/applications")
                        .with(authenticatedUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void retrieveMissingApplicationReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/applications/9999")
                        .with(authenticatedUser()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void updateMissingApplicationReturnsNotFound() throws Exception {
        mockMvc.perform(put("/api/applications/9999")
                        .with(authenticatedUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                validApplication())))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMissingApplicationReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/applications/9999")
                        .with(authenticatedUser()))
                .andExpect(status().isNotFound());
    }

    @Test
    void blankCompanyReturnsBadRequest() throws Exception {
        Map<String, Object> invalid = Map.of(
                "company", "",
                "position", "Software Engineer"
        );

        mockMvc.perform(post("/api/applications")
                        .with(authenticatedUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.company")
                        .value("Company is required."));
    }

    @Test
    void deleteExistingApplicationReturnsNoContent() throws Exception {
        JobApplication application = createOwnedApplication();
        Long id = application.getId();

        mockMvc.perform(delete("/api/applications/" + id)
                        .with(authenticatedUser()))
                .andExpect(status().isNoContent());

        assertFalse(repository.existsById(id));
    }

    @Test
    void updateStatusChangesApplicationStatus() throws Exception {
        JobApplication application = createOwnedApplication();
        Long id = application.getId();

        mockMvc.perform(patch("/api/applications/" + id + "/status")
                        .with(authenticatedUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"INTERVIEW"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.status").value("INTERVIEW"));

        assertEquals(
                "INTERVIEW",
                repository.findById(id).orElseThrow().getStatus()
        );
    }

    @Test
    void invalidStatusReturnsBadRequest() throws Exception {
        JobApplication application = createOwnedApplication();
        Long id = application.getId();

        mockMvc.perform(patch("/api/applications/" + id + "/status")
                        .with(authenticatedUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"HIRED"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        assertEquals(
                "SAVED",
                repository.findById(id).orElseThrow().getStatus()
        );
    }

    @Test
    void updateStatusForMissingApplicationReturnsNotFound()
            throws Exception {
        mockMvc.perform(patch("/api/applications/9999/status")
                        .with(authenticatedUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"INTERVIEW"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void anotherUserCannotAccessApplication() throws Exception {
        JobApplication application = createOwnedApplication();

        User otherUser = userRepository.save(new User(
                "Other Test User",
                "other-" + UUID.randomUUID() + "@example.com",
                "test-password-hash"
        ));

        mockMvc.perform(get("/api/applications/" + application.getId())
                        .with(jwt().jwt(token ->
                                token.subject(otherUser.getId().toString()))))
                .andExpect(status().isNotFound());
    }
}
