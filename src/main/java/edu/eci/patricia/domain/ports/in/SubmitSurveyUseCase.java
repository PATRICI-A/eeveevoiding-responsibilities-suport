package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.domain.model.SurveyResponse;

/**
 * Input port for submitting a student wellness survey (PTR23.1).
 */
public interface SubmitSurveyUseCase {

    /**
     * Validates, persists, and returns the saved survey response.
     * All ten questions P01–P10 must be present in {@code survey.getAnswers()}.
     *
     * @param survey domain object with studentId and raw P01–P10 answers
     * @return the persisted survey response (with generated id and submittedAt)
     * @throws edu.eci.patricia.domain.exception.WellnessException if mandatory questions are missing
     */
    SurveyResponse submitSurvey(SurveyResponse survey);
}
