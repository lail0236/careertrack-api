
package com.careertrack.api;

import com.careertrack.api.entity.ApplicationStatusHistory;
import com.careertrack.api.entity.JobApplication;
import com.careertrack.api.entity.User;
import com.careertrack.api.repository.ApplicationStatusHistoryRepository;
import com.careertrack.api.repository.JobApplicationRepository;
import com.careertrack.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HistoricalAnalyticsTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JobApplicationRepository applicationRepository;

    @Autowired
    private ApplicationStatusHistoryRepository historyRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(new User(
                "History Test User",
                "history-" + UUID.randomUUID() + "@example.com",
                "test-password-hash"
        ));
    }

    private RequestPostProcessor authenticatedUser() {
        return jwt().jwt(token ->
                token.subject(testUser.getId().toString()));
    }

    @Test
    void historicalStageCountsReturnsAllStatuses() throws Exception {
        mockMvc.perform(get("/api/analytics/historical-stage-counts")
                        .with(authenticatedUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[0].status").value("SAVED"))
                .andExpect(jsonPath(
                        "$[0].applicationsEverReached").isNumber());
    }

    @Test
    void historicalFunnelIncludesHistoricalMetrics() throws Exception {
        mockMvc.perform(get("/api/analytics/funnel")
                        .with(authenticatedUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.historicalSubmittedApplications").isNumber())
                .andExpect(jsonPath(
                        "$.historicalInterviewApplications").isNumber())
                .andExpect(jsonPath(
                        "$.historicalOfferApplications").isNumber())
                .andExpect(jsonPath(
                        "$.historicalInterviewConversionPercent").isNumber())
                .andExpect(jsonPath(
                        "$.historicalOfferConversionPercent").isNumber());
    }

    @Test
    void applicationStillCountsAsInterviewAfterRejection()
            throws Exception {

        JobApplication application = new JobApplication(
                "History Test Company", "Software Engineer");
        application.setUser(testUser);
        application = applicationRepository.save(application);

        Long applicationId = application.getId();

        historyRepository.save(new ApplicationStatusHistory(
                applicationId, null, "SAVED"));

        historyRepository.save(new ApplicationStatusHistory(
                applicationId, "SAVED", "APPLIED"));

        historyRepository.save(new ApplicationStatusHistory(
                applicationId, "APPLIED", "INTERVIEW"));

        historyRepository.save(new ApplicationStatusHistory(
                applicationId, "INTERVIEW", "REJECTED"));

        mockMvc.perform(get("/api/analytics/historical-stage-counts")
                        .with(authenticatedUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$[3].status").value("INTERVIEW"))
                .andExpect(jsonPath(
                        "$[3].applicationsEverReached").isNumber());
    }

    @Test
    void historicalFunnelHandlesNoHistoryWithoutDivisionErrors()
            throws Exception {
        mockMvc.perform(get("/api/analytics/funnel")
                        .with(authenticatedUser())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.historicalInterviewConversionPercent").isNumber())
                .andExpect(jsonPath(
                        "$.historicalOfferConversionPercent").isNumber());
    }
}
