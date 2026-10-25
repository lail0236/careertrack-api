
package com.careertrack.api.analytics;

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

    @GetMapping("/status-counts")
    public List<StatusCountResponse> getStatusCounts() {
        return service.getStatusCounts();
    }

    @GetMapping("/funnel")
    public FunnelAnalyticsResponse getFunnelAnalytics() {
        return service.getFunnelAnalytics();
    }

    @GetMapping("/activity")
    public List<DailyActivityResponse> getDailyActivity() {
        return service.getDailyActivity();
    }
}
