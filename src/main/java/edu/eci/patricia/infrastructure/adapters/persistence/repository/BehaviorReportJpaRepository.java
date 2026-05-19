package edu.eci.patricia.infrastructure.adapters.persistence.repository;

import edu.eci.patricia.infrastructure.adapters.persistence.entity.BehaviorReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link BehaviorReportEntity}.
 * Provides CRUD and reporter-scoped queries for behavior reports.
 */
@Repository
public interface BehaviorReportJpaRepository extends JpaRepository<BehaviorReportEntity, UUID> {

    /**
     * Retrieves all behavior reports submitted by a specific student.
     *
     * @param reporterId the UUID of the student reporter
     * @return list of behavior report entities submitted by that student
     */
    List<BehaviorReportEntity> findByReporterId(UUID reporterId);
}
