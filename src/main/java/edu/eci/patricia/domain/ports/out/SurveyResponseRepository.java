package edu.eci.patricia.domain.ports.out;

import edu.eci.patricia.domain.model.SurveyResponse;

import java.util.Optional;

/**
 * Output port for survey response persistence (PTR23.1 / PTR23.2).
 */
public interface SurveyResponseRepository {

    /**
     * Persists a new survey response.
     *
     * @param survey the survey response to save (id and submittedAt already set)
     */
    void save(SurveyResponse survey);

    /**
     * Returns the most recent survey submitted by a student.
     * Used by PTR23.2 to generate personalised recommendations (RN-23.2.4).
     *
     * @param studentId UUID of the student
     * @return the latest survey response, or empty if the student has never submitted one
     */
    Optional<SurveyResponse> findLatestByStudentId(String studentId);
}
