
package com.careertrack.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AnalyticsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void statusCountsReturnsAllSupportedStatuses() throws Exception {
        mockMvc.perform(get("/api/analytics/status-counts"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[0].status").value("SAVED"))
                .andExpect(jsonPath("$[0].count").isNumber());
    }

    @Test
    void funnelReturnsExpectedFields() throws Exception {
        mockMvc.perform(get("/api/analytics/funnel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalApplications").isNumber())
                .andExpect(jsonPath("$.submittedApplications").isNumber())
                .andExpect(jsonPath(
                        "$.interviewOrOfferApplications").isNumber())
                .andExpect(jsonPath("$.offers").isNumber())
                .andExpect(jsonPath("$.submissionRatePercent").isNumber())
                .andExpect(jsonPath(
                        "$.interviewConversionPercent").isNumber())
                .andExpect(jsonPath("$.offerConversionPercent").isNumber());
    }

    @Test
    void dailyActivityReturnsArray() throws Exception {
        mockMvc.perform(get("/api/analytics/activity"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }
}
