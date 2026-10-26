
package com.careertrack.api.repository;

import com.careertrack.api.entity.ApplicationStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationStatusHistoryRepository
        extends JpaRepository<ApplicationStatusHistory, Long> {

    List<ApplicationStatusHistory>
            findByApplicationIdOrderByChangedAtAscIdAsc(
                    Long applicationId);

    @Query("""
            SELECT COUNT(DISTINCT h.applicationId)
            FROM ApplicationStatusHistory h
            JOIN JobApplication a ON a.id = h.applicationId
            WHERE a.user.id = :userId
              AND h.newStatus IN :statuses
            """)
    long countDistinctApplicationsByStatusesAndUserId(
            @Param("statuses") List<String> statuses,
            @Param("userId") Long userId);
}
