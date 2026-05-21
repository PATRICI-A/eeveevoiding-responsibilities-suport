package edu.eci.patricia.application.service;

import edu.eci.patricia.application.dto.RecommendedCategoryResponse;
import edu.eci.patricia.application.dto.RecommendationResponse;
import edu.eci.patricia.domain.exception.WellnessException;
import edu.eci.patricia.domain.model.SurveyResponse;
import edu.eci.patricia.domain.model.WellnessCategory;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.ports.in.GetRecommendationsUseCase;
import edu.eci.patricia.domain.ports.in.SubmitSurveyUseCase;
import edu.eci.patricia.domain.ports.out.SurveyResponseRepository;
import edu.eci.patricia.domain.ports.out.WellnessResourceRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application service implementing PTR23.1 (survey submission) and
 * PTR23.2 (personalised recommendations).
 *
 * <p>Recommendation logic is driven by the table in PTR23.2 using the
 * raw textual answers stored in the {@code answers} map (P01–P10).</p>
 */
@Service
@RequiredArgsConstructor
public class WellnessSurveyService implements SubmitSurveyUseCase, GetRecommendationsUseCase {

    /** Complete set of mandatory question IDs (PTR23.1 / RN-23.1.1). */
    private static final Set<String> MANDATORY_QUESTIONS = Set.of(
            "P01", "P02", "P03", "P04", "P05",
            "P06", "P07", "P08", "P09", "P10");

    private final SurveyResponseRepository surveyRepository;
    private final WellnessResourceRepositoryPort resourceRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // SubmitSurveyUseCase
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     *
     * <p>Validates that all ten questions are answered, generates a UUID and
     * submission timestamp, then delegates persistence to the repository.</p>
     */
    @Override
    public SurveyResponse submitSurvey(SurveyResponse survey) {
        validateAllQuestionsAnswered(survey.getAnswers());

        survey.setId(UUID.randomUUID().toString());
        survey.setSubmittedAt(LocalDateTime.now());

        surveyRepository.save(survey);
        return survey;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GetRecommendationsUseCase
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     *
     * <p>Applies the PTR23.2 logic table to the provided answers and returns
     * categories ordered by score (number of triggered conditions) descending,
     * without duplicates (RN-23.2.6).</p>
     */
    @Override
    public List<RecommendedCategoryResponse> getRecommendedCategories(Map<String, String> answers) {
        // category → {score, accumulated reason lines}
        Map<WellnessCategory, CategoryAccumulator> accumulators = new LinkedHashMap<>();

        // ── P01: mood ──────────────────────────────────────────────────────
        String p01 = get(answers, "P01");
        if (equalsAny(p01, "Mal", "Muy mal")) {
            add(accumulators, WellnessCategory.EMOTIONAL_SUPPORT,
                    "Recomendado por estado de ánimo bajo reportado en la encuesta.");
        }

        // ── P02: academic stress ───────────────────────────────────────────
        // Values are ordinal 1-5 where >=4 means high stress
        String p02 = get(answers, "P02");
        if (numericGte(p02, 4)) {
            add(accumulators, WellnessCategory.EMOTIONAL_SUPPORT,
                    "Recomendado por nivel alto de estrés académico.");
        }

        // ── P03: physical activity frequency ──────────────────────────────
        String p03 = get(answers, "P03");
        if (equalsAny(p03, "Nunca", "1-2 veces", "1–2 veces")) {
            add(accumulators, WellnessCategory.SPORTS,
                    "Recomendado por baja frecuencia de actividad física.");
        }

        // ── P04: sleep quality ─────────────────────────────────────────────
        String p04 = get(answers, "P04");
        if (equalsAny(p04, "Mala", "Muy mala")) {
            add(accumulators, WellnessCategory.HEALTH,
                    "Recomendado por baja calidad del sueño reportada.");
        }

        // ── P05: eating habits ─────────────────────────────────────────────
        String p05 = get(answers, "P05");
        if (equalsAny(p05, "Casi nunca", "No")) {
            add(accumulators, WellnessCategory.HEALTH,
                    "Recomendado por hábitos alimenticios irregulares.");
        }

        // ── P06: social support perception ────────────────────────────────
        String p06 = get(answers, "P06");
        if (equalsAny(p06, "No mucho", "No, me siento solo/a")) {
            add(accumulators, WellnessCategory.EMOTIONAL_SUPPORT,
                    "Recomendado por baja percepción de apoyo social.");
        }

        // ── P07: cultural / social participation ──────────────────────────
        String p07 = get(answers, "P07");
        if (equalsAny(p07, "Rara vez", "Nunca")) {
            add(accumulators, WellnessCategory.CULTURE,
                    "Recomendado por baja participación en actividades culturales o sociales del campus.");
        }

        // ── P08: academic performance satisfaction ────────────────────────
        String p08 = get(answers, "P08");
        if (numericLte(p08, 2)) {
            add(accumulators, WellnessCategory.EMOTIONAL_SUPPORT,
                    "Recomendado por baja satisfacción con el rendimiento académico.");
        }

        // ── P09: physical symptoms ────────────────────────────────────────
        String p09 = get(answers, "P09");
        if (equalsAny(p09, "Con frecuencia", "Casi siempre")) {
            add(accumulators, WellnessCategory.HEALTH,
                    "Recomendado por presencia frecuente de síntomas físicos como fatiga, " +
                            "dolor de cabeza o tensión muscular.");
        }

        // ── P10: support areas (multi-value, comma-separated) ─────────────
        String p10 = get(answers, "P10");
        if (containsIgnoreCase(p10, "Actividad física")) {
            add(accumulators, WellnessCategory.SPORTS,
                    "Recomendado porque el estudiante indicó necesidad de apoyo en actividad física.");
        }
        if (containsIgnoreCase(p10, "Alimentación") || containsIgnoreCase(p10, "Salud física")) {
            add(accumulators, WellnessCategory.HEALTH,
                    "Recomendado porque el estudiante indicó necesidad de apoyo en salud o alimentación.");
        }
        if (containsIgnoreCase(p10, "Manejo del estrés") || containsIgnoreCase(p10, "Apoyo emocional")) {
            add(accumulators, WellnessCategory.EMOTIONAL_SUPPORT,
                    "Recomendado porque el estudiante indicó necesidad de apoyo emocional o manejo del estrés.");
        }
        if (containsIgnoreCase(p10, "Integración social")) {
            add(accumulators, WellnessCategory.CULTURE,
                    "Recomendado porque el estudiante indicó necesidad de mayor integración social.");
        }

        // Build result sorted by score descending (RN-23.2.6)
        return accumulators.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().score, a.getValue().score))
                .map(e -> RecommendedCategoryResponse.builder()
                        .category(e.getKey())
                        .score(e.getValue().score)
                        .reason(String.join(" ", e.getValue().reasons))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * <p>Fetches the student's latest survey, derives recommended categories,
     * then collects the active resources for those categories (RN-23.2.1).
     * If no survey exists, returns all active resources with a fallback flag (RN-23.2.2).</p>
     */
    @Override
    public List<RecommendationResponse> getRecommendationsForStudent(String studentId) {
        return surveyRepository.findLatestByStudentId(studentId)
                .map(survey -> buildRecommendedResources(survey.getAnswers()))
                .orElseGet(this::buildFallbackResources);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    private List<RecommendationResponse> buildRecommendedResources(Map<String, String> answers) {
        List<RecommendedCategoryResponse> categories = getRecommendedCategories(answers);

        // Build a category → reason lookup for annotating resources (RN-23.2.5)
        Map<WellnessCategory, String> reasonByCategory = categories.stream()
                .collect(Collectors.toMap(
                        RecommendedCategoryResponse::getCategory,
                        RecommendedCategoryResponse::getReason,
                        (a, b) -> a));

        List<RecommendationResponse> result = new ArrayList<>();
        for (RecommendedCategoryResponse cat : categories) {
            for (WellnessResource resource : resourceRepository.findByCategory(cat.getCategory())) {
                if (resource.isAvailable()) {
                    result.add(toResponse(resource, reasonByCategory.get(cat.getCategory())));
                }
            }
        }
        return result;
    }

    private List<RecommendationResponse> buildFallbackResources() {
        // RN-23.2.2: return all active resources from all categories
        List<RecommendationResponse> result = new ArrayList<>();
        for (WellnessCategory cat : WellnessCategory.values()) {
            for (WellnessResource resource : resourceRepository.findByCategory(cat)) {
                if (resource.isAvailable()) {
                    result.add(toResponse(resource, null));
                }
            }
        }
        return result;
    }

    private RecommendationResponse toResponse(WellnessResource resource, String reason) {
        return RecommendationResponse.builder()
                .id(resource.getId().toString())
                .name(resource.getName())
                .description(resource.getDescription())
                .category(resource.getCategory())
                .location(resource.getLocation())
                .contactInfo(resource.getContactInfo())
                .schedule(resource.getSchedule())
                .recommendationReason(reason)
                .build();
    }

    private void validateAllQuestionsAnswered(Map<String, String> answers) {
        if (answers == null) {
            throw new WellnessException("surveyResponses must not be null");
        }
        List<String> missing = MANDATORY_QUESTIONS.stream()
                .filter(q -> !answers.containsKey(q) || answers.get(q).isBlank())
                .sorted()
                .collect(Collectors.toList());
        if (!missing.isEmpty()) {
            throw new WellnessException(
                    "Missing answers for mandatory questions: " + missing);
        }
    }

    // ── Micro-helpers for answer comparison ───────────────────────────────

    private String get(Map<String, String> answers, String key) {
        return answers == null ? "" : answers.getOrDefault(key, "");
    }

    private boolean equalsAny(String value, String... options) {
        if (value == null) return false;
        for (String opt : options) {
            if (value.trim().equalsIgnoreCase(opt.trim())) return true;
        }
        return false;
    }

    /** Interprets the answer as a numeric value and checks if it is >= threshold. */
    private boolean numericGte(String value, int threshold) {
        try {
            return Integer.parseInt(value.trim()) >= threshold;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** Interprets the answer as a numeric value and checks if it is <= threshold. */
    private boolean numericLte(String value, int threshold) {
        try {
            return Integer.parseInt(value.trim()) <= threshold;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean containsIgnoreCase(String value, String substring) {
        return value != null && value.toLowerCase().contains(substring.toLowerCase());
    }

    /** Accumulates score and reasons for a given category (used during recommendation building). */
    private void add(Map<WellnessCategory, CategoryAccumulator> map, WellnessCategory cat, String reason) {
        map.computeIfAbsent(cat, k -> new CategoryAccumulator()).append(reason);
    }

    /** Mutable accumulator for a single category's score and reason list. */
    private static class CategoryAccumulator {
        int score = 0;
        List<String> reasons = new ArrayList<>();

        void append(String reason) {
            score++;
            if (!reasons.contains(reason)) {
                reasons.add(reason);
            }
        }
    }
}
