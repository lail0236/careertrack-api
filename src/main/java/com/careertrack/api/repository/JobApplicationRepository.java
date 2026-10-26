
package com.careertrack.api.repository;

import com.careertrack.api.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository
        extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findAllByUserId(Long userId);

    Optional<JobApplication> findByIdAndUserId(Long id, Long userId);

    long countByUserIdAndStatus(Long userId, String status);

    long countByUserId(Long userId);

    long countByUserIdAndStatusIn(Long userId, List<String> statuses);

    @Query(value = """
            SELECT
                CAST(created_at AS DATE) AS "activityDate",
                COUNT(*) AS "applicationCount"
            FROM job_applications
            WHERE user_id = :userId
            GROUP BY CAST(created_at AS DATE)
            ORDER BY CAST(created_at AS DATE)
            """, nativeQuery = true)
    List<DailyApplicationActivity> findDailyApplicationActivity(
            @Param("userId") Long userId);
}
