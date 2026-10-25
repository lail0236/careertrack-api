
package com.careertrack.api.repository;

import java.time.LocalDate;

public interface DailyApplicationActivity {

    LocalDate getActivityDate();

    Long getApplicationCount();
}
