
package com.careertrack.api.analytics;

public record HistoricalStageCountResponse(
        String status,
        long applicationsEverReached
) {}
