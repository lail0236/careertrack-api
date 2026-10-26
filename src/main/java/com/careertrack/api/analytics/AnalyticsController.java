
package com.careertrack.api.analytics;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService service;

    public AnalyticsController(AnalyticsService service) {
        this.service = service;
    }

    private Long authenticatedUserId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }

    @GetMapping("/status-counts")
    public List<StatusCountResponse> getStatusCounts(
            @AuthenticationPrincipal Jwt jwt) {
        return service.getStatusCounts(authenticatedUserId(jwt));
    }

    @GetMapping("/historical-stage-counts")
    public List<HistoricalStageCountResponse> getHistoricalStageCounts(
            @AuthenticationPrincipal Jwt jwt) {
        return service.getHistoricalStageCounts(authenticatedUserId(jwt));
    }

    @GetMapping("/funnel")
    public FunnelAnalyticsResponse getFunnelAnalytics(
            @AuthenticationPrincipal Jwt jwt) {
        return service.getFunnelAnalytics(authenticatedUserId(jwt));
    }

    @GetMapping("/activity")
    public List<DailyActivityResponse> getDailyActivity(
            @AuthenticationPrincipal Jwt jwt) {
        return service.getDailyActivity(authenticatedUserId(jwt));
    }
}
