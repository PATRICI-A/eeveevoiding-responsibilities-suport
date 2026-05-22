package edu.eci.patricia.application.service;

import edu.eci.patricia.application.dto.RecommendedCategoryResponse;
import edu.eci.patricia.application.dto.RecommendationResponse;
import edu.eci.patricia.domain.exception.WellnessException;
import edu.eci.patricia.domain.model.SurveyResponse;
import edu.eci.patricia.domain.model.WellnessCategory;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.ports.out.SurveyResponseRepository;
import edu.eci.patricia.domain.ports.out.WellnessResourceRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WellnessSurveyServiceTest {

    @Mock
    private SurveyResponseRepository surveyRepository;

    @Mock
    private WellnessResourceRepositoryPort resourceRepository;

    @InjectMocks
    private WellnessSurveyService service;

    @Captor
    private ArgumentCaptor<SurveyResponse> surveyCaptor;

    private WellnessResource emotionalResource;
    private WellnessResource sportsResource;
    private WellnessResource healthResource;
    private WellnessResource cultureResource;

    @BeforeEach
    void setUp() {
        emotionalResource = WellnessResource.builder()
                .id(UUID.randomUUID())
                .name("Counseling Center")
                .category(WellnessCategory.EMOTIONAL_SUPPORT)
                .available(true)
                .build();

        sportsResource = WellnessResource.builder()
                .id(UUID.randomUUID())
                .name("Sports Center")
                .category(WellnessCategory.SPORTS)
                .available(true)
                .build();

        healthResource = WellnessResource.builder()
                .id(UUID.randomUUID())
                .name("Health Clinic")
                .category(WellnessCategory.HEALTH)
                .available(true)
                .build();

        cultureResource = WellnessResource.builder()
                .id(UUID.randomUUID())
                .name("Cultural Hub")
                .category(WellnessCategory.CULTURE)
                .available(true)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // submitSurvey() tests
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("submitSurvey persists survey with all 10 answers and sets id + timestamp")
    void submitSurvey_withAllAnswers_persistsSuccessfully() {
        Map<String, String> answers = fullAnswers();
        SurveyResponse input = SurveyResponse.builder()
                .studentId("student-123")
                .answers(answers)
                .build();

        SurveyResponse result = service.submitSurvey(input);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getStudentId()).isEqualTo("student-123");
        assertThat(result.getAnswers()).hasSize(10);
        assertThat(result.getSubmittedAt()).isNotNull();
        verify(surveyRepository).save(surveyCaptor.capture());
        assertThat(surveyCaptor.getValue().getId()).isNotNull();
    }

    @Test
    @DisplayName("submitSurvey throws WellnessException when answers is null")
    void submitSurvey_nullAnswers_throwsException() {
        SurveyResponse input = SurveyResponse.builder()
                .studentId("student-123")
                .answers(null)
                .build();

        assertThatThrownBy(() -> service.submitSurvey(input))
                .isInstanceOf(WellnessException.class)
                .hasMessageContaining("surveyResponses must not be null");
    }

    @Test
    @DisplayName("submitSurvey throws WellnessException when some answers are missing")
    void submitSurvey_missingQuestions_throwsException() {
        Map<String, String> partial = new HashMap<>();
        partial.put("P01", "Bien");
        partial.put("P02", "3");

        SurveyResponse input = SurveyResponse.builder()
                .studentId("student-123")
                .answers(partial)
                .build();

        assertThatThrownBy(() -> service.submitSurvey(input))
                .isInstanceOf(WellnessException.class)
                .hasMessageContaining("Missing answers");
    }

    @Test
    @DisplayName("submitSurvey throws WellnessException when answer is blank")
    void submitSurvey_blankAnswer_throwsException() {
        Map<String, String> answers = fullAnswers();
        answers.put("P01", "");

        SurveyResponse input = SurveyResponse.builder()
                .studentId("student-123")
                .answers(answers)
                .build();

        assertThatThrownBy(() -> service.submitSurvey(input))
                .isInstanceOf(WellnessException.class)
                .hasMessageContaining("Missing answers");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // getRecommendedCategories() tests — full PTR23.2 logic table
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("P01=Mal triggers EMOTIONAL_SUPPORT")
    void p01_mal_returnsEmotionalSupport() {
        Map<String, String> answers = fullAnswers();
        answers.put("P01", "Mal");

        List<RecommendedCategoryResponse> result = service.getRecommendedCategories(answers);

        assertThat(result).anyMatch(r -> r.getCategory() == WellnessCategory.EMOTIONAL_SUPPORT);
    }

    @Test
    @DisplayName("P01=Muy mal triggers EMOTIONAL_SUPPORT")
    void p01_muyMal_returnsEmotionalSupport() {
        Map<String, String> answers = fullAnswers();
        answers.put("P01", "Muy mal");

        List<RecommendedCategoryResponse> result = service.getRecommendedCategories(answers);

        assertThat(result).anyMatch(r -> r.getCategory() == WellnessCategory.EMOTIONAL_SUPPORT);
    }

    @Test
    @DisplayName("P02>=4 triggers EMOTIONAL_SUPPORT")
    void p02_highStress_returnsEmotionalSupport() {
        Map<String, String> answers = fullAnswers();
        answers.put("P02", "4");

        List<RecommendedCategoryResponse> result = service.getRecommendedCategories(answers);

        assertThat(result).anyMatch(r -> r.getCategory() == WellnessCategory.EMOTIONAL_SUPPORT);
    }

    @Test
    @DisplayName("P03=Nunca triggers SPORTS")
    void p03_nunca_returnsSports() {
        Map<String, String> answers = fullAnswers();
        answers.put("P03", "Nunca");

        List<RecommendedCategoryResponse> result = service.getRecommendedCategories(answers);

        assertThat(result).anyMatch(r -> r.getCategory() == WellnessCategory.SPORTS);
    }

    @Test
    @DisplayName("P03=1-2 veces triggers SPORTS")
    void p03_unoDosVeces_returnsSports() {
        Map<String, String> answers = fullAnswers();
        answers.put("P03", "1-2 veces");

        List<RecommendedCategoryResponse> result = service.getRecommendedCategories(answers);

        assertThat(result).anyMatch(r -> r.getCategory() == WellnessCategory.SPORTS);
    }

    @Test
    @DisplayName("P04=Mala triggers HEALTH")
    void p04_mala_returnsHealth() {
        Map<String, String> answers = fullAnswers();
        answers.put("P04", "Mala");

        List<RecommendedCategoryResponse> result = service.getRecommendedCategories(answers);

        assertThat(result).anyMatch(r -> r.getCategory() == WellnessCategory.HEALTH);
    }

    @Test
    @DisplayName("P04=Muy mala triggers HEALTH")
    void p04_muyMala_returnsHealth() {
        Map<String, String> answers = fullAnswers();
        answers.put("P04", "Muy mala");

        List<RecommendedCategoryResponse> result = service.getRecommendedCategories(answers);

        assertThat(result).anyMatch(r -> r.getCategory() == WellnessCategory.HEALTH);
    }

    @Test
    @DisplayName("P05=Casi nunca triggers HEALTH")
    void p05_casiNunca_returnsHealth() {
        Map<String, String> answers = fullAnswers();
        answers.put("P05", "Casi nunca");

        List<RecommendedCategoryResponse> result = service.getRecommendedCategories(answers);

        assertThat(result).anyMatch(r -> r.getCategory() == WellnessCategory.HEALTH);
    }

    @Test
    @DisplayName("P05=No triggers HEALTH")
    void p05_no_returnsHealth() {
        Map<String, String> answers = fullAnswers();
        answers.put("P05", "No");

        List<RecommendedCategoryResponse> result = service.getRecommendedCategories(answers);

        assertThat(result).anyMatch(r -> r.getCategory() == WellnessCategory.HEALTH);
    }

    @Test
    @DisplayName("P06=No mucho triggers EMOTIONAL_SUPPORT")
    void p06_noMucho_returnsEmotionalSupport() {
        Map<String, String> answers = fullAnswers();
        answers.put("P06", "No mucho");

        List<RecommendedCategoryResponse> result = service.getRecommendedCategories(answers);

        assertThat(result).anyMatch(r -> r.getCategory() == WellnessCategory.EMOTIONAL_SUPPORT);
    }

    @Test
    @DisplayName("P06=No, me siento solo/a triggers EMOTIONAL_SUPPORT")
    void p06_meSientoSolo_returnsEmotionalSupport() {
        Map<String, String> answers = fullAnswers();
        answers.put("P06", "No, me siento solo/a");

        List<RecommendedCategoryResponse> result = service.getRecommendedCategories(answers);

        assertThat(result).anyMatch(r -> r.getCategory() == WellnessCategory.EMOTIONAL_SUPPORT);
    }

    @Test
    @DisplayName("P07=Rara vez triggers CULTURE")
    void p07_raraVez_returnsCulture() {
        Map<String, String> answers = fullAnswers();
        answers.put("P07", "Rara vez");

        List<RecommendedCategoryResponse> result = service.getRecommendedCategories(answers);

        assertThat(result).anyMatch(r -> r.getCategory() == WellnessCategory.CULTURE);
    }

    @Test
    @DisplayName("P07=Nunca triggers CULTURE")
    void p07_nunca_returnsCulture() {
        Map<String, String> answers = fullAnswers();
        answers.put("P07", "Nunca");

        List<RecommendedCategoryResponse> result = service.getRecommendedCategories(answers);

        assertThat(result).anyMatch(r -> r.getCategory() == WellnessCategory.CULTURE);
    }

    @Test
    @DisplayName("P08<=2 triggers EMOTIONAL_SUPPORT")
    void p08_lowSatisfaction_returnsEmotionalSupport() {
        Map<String, String> answers = fullAnswers();
        answers.put("P08", "2");

        List<RecommendedCategoryResponse> result = service.getRecommendedCategories(answers);

        assertThat(result).anyMatch(r -> r.getCategory() == WellnessCategory.EMOTIONAL_SUPPORT);
    }

    @Test
    @DisplayName("P09=Con frecuencia triggers HEALTH")
    void p09_conFrecuencia_returnsHealth() {
        Map<String, String> answers = fullAnswers();
        answers.put("P09", "Con frecuencia");

        List<RecommendedCategoryResponse> result = service.getRecommendedCategories(answers);

        assertThat(result).anyMatch(r -> r.getCategory() == WellnessCategory.HEALTH);
    }

    @Test
    @DisplayName("P09=Casi siempre triggers HEALTH")
    void p09_casiSiempre_returnsHealth() {
        Map<String, String> answers = fullAnswers();
        answers.put("P09", "Casi siempre");

        List<RecommendedCategoryResponse> result = service.getRecommendedCategories(answers);

        assertThat(result).anyMatch(r -> r.getCategory() == WellnessCategory.HEALTH);
    }

    @Test
    @DisplayName("P10=Actividad física triggers SPORTS")
    void p10_actividadFisica_returnsSports() {
        Map<String, String> answers = fullAnswers();
        answers.put("P10", "Actividad física");

        List<RecommendedCategoryResponse> result = service.getRecommendedCategories(answers);

        assertThat(result).anyMatch(r -> r.getCategory() == WellnessCategory.SPORTS);
    }

    @Test
    @DisplayName("P10=Alimentación triggers HEALTH")
    void p10_alimentacion_returnsHealth() {
        Map<String, String> answers = fullAnswers();
        answers.put("P10", "Alimentación");

        List<RecommendedCategoryResponse> result = service.getRecommendedCategories(answers);

        assertThat(result).anyMatch(r -> r.getCategory() == WellnessCategory.HEALTH);
    }

    @Test
    @DisplayName("P10=Salud física triggers HEALTH")
    void p10_saludFisica_returnsHealth() {
        Map<String, String> answers = fullAnswers();
        answers.put("P10", "Salud física");

        List<RecommendedCategoryResponse> result = service.getRecommendedCategories(answers);

        assertThat(result).anyMatch(r -> r.getCategory() == WellnessCategory.HEALTH);
    }

    @Test
    @DisplayName("P10=Manejo del estrés triggers EMOTIONAL_SUPPORT")
    void p10_manejoEstres_returnsEmotionalSupport() {
        Map<String, String> answers = fullAnswers();
        answers.put("P10", "Manejo del estrés");

        List<RecommendedCategoryResponse> result = service.getRecommendedCategories(answers);

        assertThat(result).anyMatch(r -> r.getCategory() == WellnessCategory.EMOTIONAL_SUPPORT);
    }

    @Test
    @DisplayName("P10=Apoyo emocional triggers EMOTIONAL_SUPPORT")
    void p10_apoyoEmocional_returnsEmotionalSupport() {
        Map<String, String> answers = fullAnswers();
        answers.put("P10", "Apoyo emocional");

        List<RecommendedCategoryResponse> result = service.getRecommendedCategories(answers);

        assertThat(result).anyMatch(r -> r.getCategory() == WellnessCategory.EMOTIONAL_SUPPORT);
    }

    @Test
    @DisplayName("P10=Integración social triggers CULTURE")
    void p10_integracionSocial_returnsCulture() {
        Map<String, String> answers = fullAnswers();
        answers.put("P10", "Integración social");

        List<RecommendedCategoryResponse> result = service.getRecommendedCategories(answers);

        assertThat(result).anyMatch(r -> r.getCategory() == WellnessCategory.CULTURE);
    }

    @Test
    @DisplayName("Multiple P10 values trigger multiple categories")
    void p10_multipleValues_returnsMultipleCategories() {
        Map<String, String> answers = fullAnswers();
        answers.put("P10", "Actividad física, Manejo del estrés, Integración social");

        List<RecommendedCategoryResponse> result = service.getRecommendedCategories(answers);

        assertThat(result).anyMatch(r -> r.getCategory() == WellnessCategory.SPORTS);
        assertThat(result).anyMatch(r -> r.getCategory() == WellnessCategory.EMOTIONAL_SUPPORT);
        assertThat(result).anyMatch(r -> r.getCategory() == WellnessCategory.CULTURE);
    }

    @Test
    @DisplayName("Results sorted by score descending without duplicates")
    void results_sortedByScoreDescending_noDuplicates() {
        Map<String, String> answers = fullAnswers();
        answers.put("P01", "Mal");                                    // EMOTIONAL_SUPPORT +1
        answers.put("P02", "5");                                      // EMOTIONAL_SUPPORT +1
        answers.put("P06", "No mucho");                               // EMOTIONAL_SUPPORT +1
        answers.put("P08", "1");                                      // EMOTIONAL_SUPPORT +1
        answers.put("P10", "Apoyo emocional");                        // EMOTIONAL_SUPPORT +1

        List<RecommendedCategoryResponse> result = service.getRecommendedCategories(answers);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo(WellnessCategory.EMOTIONAL_SUPPORT);
        assertThat(result.get(0).getScore()).isEqualTo(5);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // getRecommendationsForStudent() tests
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getRecommendationsForStudent returns recommended resources when survey exists")
    void getRecommendationsForStudent_withSurvey_returnsRecommendations() {
        Map<String, String> answers = fullAnswers();
        answers.put("P04", "Mala");
        answers.put("P09", "Con frecuencia");

        SurveyResponse survey = SurveyResponse.builder()
                .id(UUID.randomUUID().toString())
                .studentId("student-123")
                .answers(answers)
                .submittedAt(LocalDateTime.now())
                .build();

        when(surveyRepository.findLatestByStudentId("student-123")).thenReturn(Optional.of(survey));
        when(resourceRepository.findByCategory(WellnessCategory.HEALTH)).thenReturn(List.of(healthResource));

        List<RecommendationResponse> result = service.getRecommendationsForStudent("student-123");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Health Clinic");
        assertThat(result.get(0).getRecommendationReason()).isNotNull();
    }

    @Test
    @DisplayName("getRecommendationsForStudent returns fallback all resources when no survey")
    void getRecommendationsForStudent_noSurvey_returnsFallback() {
        when(surveyRepository.findLatestByStudentId("new-student")).thenReturn(Optional.empty());
        when(resourceRepository.findByCategory(WellnessCategory.EMOTIONAL_SUPPORT)).thenReturn(List.of(emotionalResource));
        when(resourceRepository.findByCategory(WellnessCategory.HEALTH)).thenReturn(List.of(healthResource));
        when(resourceRepository.findByCategory(WellnessCategory.SPORTS)).thenReturn(List.of(sportsResource));
        when(resourceRepository.findByCategory(WellnessCategory.CULTURE)).thenReturn(List.of(cultureResource));

        List<RecommendationResponse> result = service.getRecommendationsForStudent("new-student");

        assertThat(result).hasSize(4);
    }

    @Test
    @DisplayName("getRecommendationsForStudent filters unavailable resources")
    void getRecommendationsForStudent_filtersUnavailable() {
        WellnessResource unavailable = WellnessResource.builder()
                .id(UUID.randomUUID())
                .name("Closed Resource")
                .category(WellnessCategory.HEALTH)
                .available(false)
                .build();

        Map<String, String> answers = fullAnswers();
        answers.put("P04", "Mala");

        SurveyResponse survey = SurveyResponse.builder()
                .id(UUID.randomUUID().toString())
                .studentId("student-123")
                .answers(answers)
                .submittedAt(LocalDateTime.now())
                .build();

        when(surveyRepository.findLatestByStudentId("student-123")).thenReturn(Optional.of(survey));
        when(resourceRepository.findByCategory(WellnessCategory.HEALTH)).thenReturn(List.of(unavailable));

        List<RecommendationResponse> result = service.getRecommendationsForStudent("student-123");

        assertThat(result).isEmpty();
    }

    private Map<String, String> fullAnswers() {
        Map<String, String> answers = new HashMap<>();
        answers.put("P01", "Bien");
        answers.put("P02", "3");
        answers.put("P03", "Todos los días");
        answers.put("P04", "Buena");
        answers.put("P05", "Sí, siempre");
        answers.put("P06", "Sí, completamente");
        answers.put("P07", "Con frecuencia");
        answers.put("P08", "4");
        answers.put("P09", "A veces");
        answers.put("P10", "Ninguna");
        return answers;
    }
}
