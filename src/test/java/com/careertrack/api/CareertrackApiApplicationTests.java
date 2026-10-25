
package com.careertrack.api;

import com.careertrack.api.entity.JobApplication;
import com.careertrack.api.repository.JobApplicationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CareertrackApiApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JobApplicationRepository repository;

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    @Test
    void createApplicationReturnsCreatedRecord() throws Exception {
        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validApplication())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.company").value("Microsoft"))
                .andExpect(jsonPath("$.status").value("SAVED"));
    }

    @Test
    void listApplicationsReturnsArray() throws Exception {
        mockMvc.perform(get("/api/applications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void retrieveMissingApplicationReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/applications/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void updateMissingApplicationReturnsNotFound() throws Exception {
        mockMvc.perform(put("/api/applications/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validApplication())))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMissingApplicationReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/applications/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void blankCompanyReturnsBadRequest() throws Exception {
        Map<String, Object> invalid = Map.of(
                "company", "",
                "position", "Software Engineer"
        );

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.company")
                        .value("Company is required."));
    }

    @Test
    void deleteExistingApplicationReturnsNoContent() throws Exception {
        JobApplication application =
                repository.save(new JobApplication("Test Company", "Test Position"));

        Long id = application.getId();

        mockMvc.perform(delete("/api/applications/" + id))
                .andExpect(status().isNoContent());

        assertFalse(repository.existsById(id));
    }
}
