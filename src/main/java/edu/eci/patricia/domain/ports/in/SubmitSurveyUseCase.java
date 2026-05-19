package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.domain.model.SurveyResponse;

import java.util.List;
import java.util.UUID;

/**
 * Input port for submitting and querying student wellness surveys.
 */
public interface SubmitSurveyUseCase {

    /**
     * Processes and stores a new wellness survey submitted by a student.
     *
     * @param survey the survey data including all five dimension scores
     * @return the persisted survey response with computed average and wellbeing level
     */
    SurveyResponse submitSurvey(SurveyResponse survey);

    /**
     * Returns the survey history for a specific student.
     *
     * @param userId the UUID of the student whose history is requested
     * @return list of all survey responses submitted by that student, ordered by submission date
     */
    List<SurveyResponse> getSurveyHistory(UUID userId);
}
