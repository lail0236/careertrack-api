
package com.careertrack.api.analytics;

import com.careertrack.api.repository.ApplicationStatusHistoryRepository;
import com.careertrack.api.repository.DailyApplicationActivity;
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

    private static final List<String> SUBMITTED_STATUSES = List.of(
            "APPLIED",
            "ASSESSMENT",
            "INTERVIEW",
            "OFFER",
            "REJECTED"
    );

    private static final List<String> INTERVIEW_STATUSES = List.of(
            "INTERVIEW",
            "OFFER"
    );

    private final JobApplicationRepository repository;
    private final ApplicationStatusHistoryRepository historyRepository;

    public AnalyticsService(
            JobApplicationRepository repository,
            ApplicationStatusHistoryRepository historyRepository) {
        this.repository = repository;
        this.historyRepository = historyRepository;
    }

    public List<StatusCountResponse> getStatusCounts() {
        return STATUSES.stream()
                .map(status -> new StatusCountResponse(
                        status,
                        repository.countByStatus(status)
                ))
                .toList();
    }

    public List<HistoricalStageCountResponse> getHistoricalStageCounts() {
        return STATUSES.stream()
                .map(status -> new HistoricalStageCountResponse(
                        status,
                        historyRepository
                                .countDistinctApplicationsByStatuses(
                                        List.of(status))
                ))
                .toList();
    }

    public FunnelAnalyticsResponse getFunnelAnalytics() {
        long total = repository.count();
        long saved = repository.countByStatus("SAVED");
        long submitted = total - saved;

        long interviewOrOffer =
                repository.countByStatusIn(INTERVIEW_STATUSES);

        long offers = repository.countByStatus("OFFER");

        long historicalSubmitted =
                historyRepository.countDistinctApplicationsByStatuses(
                        SUBMITTED_STATUSES);

        long historicalInterviews =
                historyRepository.countDistinctApplicationsByStatuses(
                        INTERVIEW_STATUSES);

        long historicalOffers =
                historyRepository.countDistinctApplicationsByStatuses(
                        List.of("OFFER"));

        return new FunnelAnalyticsResponse(
                total,
                submitted,
                interviewOrOffer,
                offers,
                percentage(submitted, total),
                percentage(interviewOrOffer, submitted),
                percentage(offers, submitted),
                historicalSubmitted,
                historicalInterviews,
                historicalOffers,
                percentage(historicalInterviews, historicalSubmitted),
                percentage(historicalOffers, historicalSubmitted)
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
