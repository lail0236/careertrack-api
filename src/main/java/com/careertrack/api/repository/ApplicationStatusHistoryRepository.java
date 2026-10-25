
package com.careertrack.api.repository;

import com.careertrack.api.entity.ApplicationStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationStatusHistoryRepository
        extends JpaRepository<ApplicationStatusHistory, Long> {

    List<ApplicationStatusHistory>
            findByApplicationIdOrderByChangedAtAscIdAsc(
                    Long applicationId);
}
