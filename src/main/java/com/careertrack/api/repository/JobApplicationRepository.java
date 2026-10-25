
package com.careertrack.api.repository;

import com.careertrack.api.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository
        extends JpaRepository<JobApplication, Long> {

    long countByStatus(String status);

    long countByStatusNot(String status);

    long countByStatusIn(Iterable<String> statuses);

    @Query(value = """
            SELECT
                CAST(created_at AS DATE) AS "activityDate",
                COUNT(*) AS "applicationCount"
            FROM job_applications
            GROUP BY CAST(created_at AS DATE)
            ORDER BY CAST(created_at AS DATE)
            """, nativeQuery = true)
    List<DailyApplicationActivity> findDailyApplicationActivity();
}
