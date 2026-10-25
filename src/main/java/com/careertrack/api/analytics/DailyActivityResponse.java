
package com.careertrack.api.analytics;

import java.time.LocalDate;

public record DailyActivityResponse(
        LocalDate date,
        long applicationCount
) {}
