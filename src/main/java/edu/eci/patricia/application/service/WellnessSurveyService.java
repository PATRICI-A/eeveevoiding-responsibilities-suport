package edu.eci.patricia.application.service;

import edu.eci.patricia.domain.model.SurveyResponse;
import edu.eci.patricia.domain.model.WellbeingLevel;
import edu.eci.patricia.domain.model.WellnessCategory;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.ports.in.GetRecommendationsUseCase;
import edu.eci.patricia.domain.ports.in.SubmitSurveyUseCase;
import edu.eci.patricia.domain.ports.out.SurveyResponseRepositoryPort;
import edu.eci.patricia.domain.ports.out.WellnessResourceRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Application service implementing wellness survey submission and recommendation logic.
 * Scores are evaluated per dimension to recommend relevant campus resources.
 */
@Service
@RequiredArgsConstructor
public class WellnessSurveyService implements SubmitSurveyUseCase, GetRecommendationsUseCase {

    private static final int SCORE_THRESHOLD = 3;

    private final SurveyResponseRepositoryPort surveyRepositoryPort;
    private final WellnessResourceRepositoryPort resourceRepositoryPort;

    /**
     * {@inheritDoc}
     * Computes the average score, determines the wellbeing level, and persists the response.
     */
    @Override
    public SurveyResponse submitSurvey(SurveyResponse survey) {
        double average = computeAverage(survey.getMoodScore(), survey.getStressScore(),
                survey.getSleepScore(), survey.getSocialScore(), survey.getAcademicScore());
        survey.setAverageScore(average);
        survey.setWellbeingLevel(computeWellbeingLevel(average));
        survey.setSubmittedAt(LocalDateTime.now());
        return surveyRepositoryPort.save(survey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SurveyResponse> getSurveyHistory(UUID userId) {
        return surveyRepositoryPort.findByUserId(userId);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Recommendation rules (aligned with RF23 WellnessCategory values):</p>
     * <ul>
     *   <li>moodScore &lt; 3    → MENTAL_HEALTH resources</li>
     *   <li>stressScore &lt; 3  → MENTAL_HEALTH + SPORTS resources</li>
     *   <li>sleepScore &lt; 3   → SPORTS resources</li>
     *   <li>socialScore &lt; 3  → CULTURE resources</li>
     *   <li>academicScore &lt; 3 → ACADEMIC_SUPPORT resources</li>
     * </ul>
     */
    @Override
    public List<WellnessResource> getRecommendations(int moodScore, int stressScore, int sleepScore,
                                                      int socialScore, int academicScore) {
        Set<WellnessCategory> categoriesToFetch = new LinkedHashSet<>();

        if (moodScore < SCORE_THRESHOLD) {
            categoriesToFetch.add(WellnessCategory.MENTAL_HEALTH);
        }
        if (stressScore < SCORE_THRESHOLD) {
            categoriesToFetch.add(WellnessCategory.MENTAL_HEALTH);
            categoriesToFetch.add(WellnessCategory.SPORTS);
        }
        if (sleepScore < SCORE_THRESHOLD) {
            categoriesToFetch.add(WellnessCategory.SPORTS);
        }
        if (socialScore < SCORE_THRESHOLD) {
            categoriesToFetch.add(WellnessCategory.CULTURE);
        }
        if (academicScore < SCORE_THRESHOLD) {
            categoriesToFetch.add(WellnessCategory.ACADEMIC_SUPPORT);
        }

        // Use a LinkedHashSet of ids to avoid duplicate resources across categories
        List<WellnessResource> result = new ArrayList<>();
        Set<UUID> seen = new LinkedHashSet<>();
        for (WellnessCategory category : categoriesToFetch) {
            for (WellnessResource resource : resourceRepositoryPort.findByCategory(category)) {
                if (resource.isAvailable() && seen.add(resource.getId())) {
                    result.add(resource);
                }
            }
        }
        return result;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Computes the arithmetic mean of the five survey dimension scores.
     *
     * @param scores individual dimension scores
     * @return the mean value rounded to two decimal places
     */
    private double computeAverage(int... scores) {
        double sum = 0;
        for (int score : scores) {
            sum += score;
        }
        double raw = sum / scores.length;
        return Math.round(raw * 100.0) / 100.0;
    }

    /**
     * Maps an average score to a WellbeingLevel classification.
     *
     * @param average the computed average score
     * @return the corresponding wellbeing level
     */
    private WellbeingLevel computeWellbeingLevel(double average) {
        if (average < 2.0) {
            return WellbeingLevel.CRITICAL;
        } else if (average < 3.0) {
            return WellbeingLevel.LOW;
        } else if (average < 3.5) {
            return WellbeingLevel.MODERATE;
        } else if (average <= 4.2) {
            return WellbeingLevel.GOOD;
        } else {
            return WellbeingLevel.EXCELLENT;
        }
    }
}
