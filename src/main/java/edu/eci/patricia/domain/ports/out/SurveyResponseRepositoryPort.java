package edu.eci.patricia.domain.ports.out;

import edu.eci.patricia.domain.model.SurveyResponse;

import java.util.List;
import java.util.UUID;

/**
 * Output port defining persistence operations for survey responses.
 */
public interface SurveyResponseRepositoryPort {

    /**
     * Persists a survey response.
     *
     * @param surveyResponse the survey response to save
     * @return the saved survey response
     */
    SurveyResponse save(SurveyResponse surveyResponse);

    /**
     * Retrieves all survey responses submitted by a specific user.
     *
     * @param userId the UUID of the student
     * @return list of survey responses ordered by submission date descending
     */
    List<SurveyResponse> findByUserId(UUID userId);
}
