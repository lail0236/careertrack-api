
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
            WHERE h.newStatus IN :statuses
            """)
    long countDistinctApplicationsByStatuses(
            @Param("statuses") List<String> statuses);
}
