
package com.careertrack.api.analytics;

public record FunnelAnalyticsResponse(
        long totalApplications,
        long submittedApplications,
        long interviewOrOfferApplications,
        long offers,
        double submissionRatePercent,
        double interviewConversionPercent,
        double offerConversionPercent,
        long historicalSubmittedApplications,
        long historicalInterviewApplications,
        long historicalOfferApplications,
        double historicalInterviewConversionPercent,
        double historicalOfferConversionPercent
) {}
