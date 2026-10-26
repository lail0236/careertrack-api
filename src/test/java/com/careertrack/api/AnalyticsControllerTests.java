
package com.careertrack.api;

import com.careertrack.api.entity.User;
import com.careertrack.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AnalyticsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(new User(
                "Analytics Test User",
                "analytics-" + java.util.UUID.randomUUID() + "@example.com",
                "test-password-hash"
        ));
    }

    private org.springframework.test.web.servlet.request.RequestPostProcessor authenticatedUser() {
        return jwt().jwt(token ->
                token.subject(testUser.getId().toString()));
    }

    @Test
    void statusCountsReturnsAllSupportedStatuses() throws Exception {
        mockMvc.perform(get("/api/analytics/status-counts")
                        .with(authenticatedUser()))
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
        mockMvc.perform(get("/api/analytics/funnel")
                        .with(authenticatedUser()))
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
        mockMvc.perform(get("/api/analytics/activity")
                        .with(authenticatedUser()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }
}
