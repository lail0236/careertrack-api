
package com.careertrack.api;

import com.careertrack.api.entity.ApplicationStatusHistory;
import com.careertrack.api.repository.ApplicationStatusHistoryRepository;
import com.careertrack.api.repository.JobApplicationRepository;
import com.careertrack.api.entity.JobApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Test
    void historicalStageCountsReturnsAllStatuses() throws Exception {
        mockMvc.perform(get("/api/analytics/historical-stage-counts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[0].status").value("SAVED"))
                .andExpect(jsonPath(
                        "$[0].applicationsEverReached").isNumber());
    }

    @Test
    void historicalFunnelIncludesHistoricalMetrics() throws Exception {
        mockMvc.perform(get("/api/analytics/funnel"))
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

        JobApplication application = applicationRepository.save(
                new JobApplication(
                        "History Test Company",
                        "Software Engineer"
                )
        );

        Long applicationId = application.getId();

        historyRepository.save(new ApplicationStatusHistory(
                applicationId, null, "SAVED"));

        historyRepository.save(new ApplicationStatusHistory(
                applicationId, "SAVED", "APPLIED"));

        historyRepository.save(new ApplicationStatusHistory(
                applicationId, "APPLIED", "INTERVIEW"));

        historyRepository.save(new ApplicationStatusHistory(
                applicationId, "INTERVIEW", "REJECTED"));

        mockMvc.perform(get("/api/analytics/historical-stage-counts"))
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
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.historicalInterviewConversionPercent").isNumber())
                .andExpect(jsonPath(
                        "$.historicalOfferConversionPercent").isNumber());
    }
}
