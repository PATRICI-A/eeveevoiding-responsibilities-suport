package edu.eci.patricia.infrastructure.adapters.persistence.repository;

import edu.eci.patricia.infrastructure.adapters.persistence.entity.SurveyResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link SurveyResponseEntity}.
 * Provides CRUD and user-scoped queries for survey responses.
 */
@Repository
public interface SurveyResponseJpaRepository extends JpaRepository<SurveyResponseEntity, UUID> {

    /**
     * Retrieves all survey responses submitted by a specific student, newest first.
     *
     * @param userId the UUID of the student
     * @return list of survey response entities ordered by submission date descending
     */
    List<SurveyResponseEntity> findByUserIdOrderBySubmittedAtDesc(UUID userId);
}
