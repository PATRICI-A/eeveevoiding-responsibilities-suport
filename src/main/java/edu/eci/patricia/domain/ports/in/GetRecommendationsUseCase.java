package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.application.dto.RecommendedCategoryResponse;
import edu.eci.patricia.application.dto.RecommendationResponse;

import java.util.List;

/**
 * Input port for generating personalized wellness recommendations (PTR23.2).
 */
public interface GetRecommendationsUseCase {

    /**
     * Derives recommended categories from a set of raw survey answers (P01–P10).
     * Applies the logic table defined in PTR23.2.
     *
     * @param answers map of questionId → selected answer (P01–P10)
     * @return list of recommended categories ordered by score descending, no duplicates (RN-23.2.6)
     */
    List<RecommendedCategoryResponse> getRecommendedCategories(java.util.Map<String, String> answers);

    /**
     * Returns active resources for the recommended categories of the student's latest survey.
     * Each resource includes a {@code recommendationReason} (RN-23.2.5).
     * If the student has not completed the survey, returns all active resources with
     * {@code fallback = true} (RN-23.2.2).
     *
     * @param studentId UUID of the student (from JWT)
     * @return list of recommended resources or fallback general resources
     */
    List<RecommendationResponse> getRecommendationsForStudent(String studentId);
}
