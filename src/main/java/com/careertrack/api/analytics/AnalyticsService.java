
package com.careertrack.api.analytics;

import com.careertrack.api.repository.JobApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private static final List<String> STATUSES = List.of(
            "SAVED",
            "APPLIED",
            "ASSESSMENT",
            "INTERVIEW",
            "OFFER",
            "REJECTED"
    );

    private final JobApplicationRepository repository;

    public AnalyticsService(JobApplicationRepository repository) {
        this.repository = repository;
    }

    public List<StatusCountResponse> getStatusCounts() {
        return STATUSES.stream()
                .map(status -> new StatusCountResponse(
                        status,
                        repository.countByStatus(status)
                ))
                .toList();
    }

    public FunnelAnalyticsResponse getFunnelAnalytics() {
        long total = repository.count();
        long saved = repository.countByStatus("SAVED");
        long submitted = total - saved;

        long interviewOrOffer =
                repository.countByStatusIn(
                        List.of("INTERVIEW", "OFFER")
                );

        long offers = repository.countByStatus("OFFER");

        return new FunnelAnalyticsResponse(
                total,
                submitted,
                interviewOrOffer,
                offers,
                percentage(submitted, total),
                percentage(interviewOrOffer, submitted),
                percentage(offers, submitted)
        );
    }

    public List<DailyActivityResponse> getDailyActivity() {
        return repository.findDailyApplicationActivity()
                .stream()
                .map(activity -> new DailyActivityResponse(
                        activity.getActivityDate(),
                        activity.getApplicationCount()
                ))
                .toList();
    }

    private double percentage(long numerator, long denominator) {
        if (denominator == 0) {
            return 0.0;
        }

        return BigDecimal.valueOf(numerator)
                .multiply(BigDecimal.valueOf(100))
                .divide(
                        BigDecimal.valueOf(denominator),
                        2,
                        RoundingMode.HALF_UP
                )
                .doubleValue();
    }
}
