package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.domain.model.WellnessResource;

import java.util.List;
import java.util.UUID;

/**
 * Input port for generating personalized wellness resource recommendations.
 * Recommendations are derived from the student's most recently submitted survey scores.
 */
public interface GetRecommendationsUseCase {

    /**
     * Returns a list of recommended wellness resources for a student based on their survey scores.
     *
     * @param moodScore     self-reported mood score (1-5)
     * @param stressScore   self-reported stress score (1-5)
     * @param sleepScore    self-reported sleep quality score (1-5)
     * @param socialScore   self-reported social connection score (1-5)
     * @param academicScore self-reported academic performance score (1-5)
     * @return deduplicated list of recommended wellness resources
     */
    List<WellnessResource> getRecommendations(int moodScore, int stressScore, int sleepScore,
                                               int socialScore, int academicScore);
}
