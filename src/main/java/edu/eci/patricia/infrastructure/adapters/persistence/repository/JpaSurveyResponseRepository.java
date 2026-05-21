package edu.eci.patricia.infrastructure.adapters.persistence.repository;

import edu.eci.patricia.infrastructure.adapters.persistence.entity.SurveyResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link SurveyResponseEntity} (PTR23.1 / PTR23.2).
 */
public interface JpaSurveyResponseRepository extends JpaRepository<SurveyResponseEntity, String> {

    /**
     * Returns the most recent survey for a student (used by PTR23.2 recommendation engine).
     *
     * @param studentId the student's UUID string
     * @return the latest survey entity, or empty if none exists
     */
    Optional<SurveyResponseEntity> findTopByStudentIdOrderBySubmittedAtDesc(String studentId);
}
