package edu.eci.patricia.application.service;

import edu.eci.patricia.domain.model.SurveyResponse;
import edu.eci.patricia.domain.model.WellbeingLevel;
import edu.eci.patricia.domain.model.WellnessCategory;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.ports.out.SurveyResponseRepositoryPort;
import edu.eci.patricia.domain.ports.out.WellnessResourceRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link WellnessSurveyService}.
 */
@ExtendWith(MockitoExtension.class)
class WellnessSurveyServiceTest {

    @Mock
    private SurveyResponseRepositoryPort surveyRepositoryPort;

    @Mock
    private WellnessResourceRepositoryPort resourceRepositoryPort;

    @InjectMocks
    private WellnessSurveyService service;

    private WellnessResource mentalHealthResource;
    private WellnessResource sportsResource;
    private WellnessResource cultureResource;
    private WellnessResource academicSupportResource;

    @BeforeEach
    void setUp() {
        mentalHealthResource = WellnessResource.builder()
                .id(UUID.randomUUID())
                .name("Counseling Center")
                .category(WellnessCategory.MENTAL_HEALTH)
                .available(true)
                .build();

        sportsResource = WellnessResource.builder()
                .id(UUID.randomUUID())
                .name("Sports Center")
                .category(WellnessCategory.SPORTS)
                .available(true)
                .build();

        cultureResource = WellnessResource.builder()
                .id(UUID.randomUUID())
                .name("Cultural Hub")
                .category(WellnessCategory.CULTURE)
                .available(true)
                .build();

        academicSupportResource = WellnessResource.builder()
                .id(UUID.randomUUID())
                .name("Academic Tutoring")
                .category(WellnessCategory.ACADEMIC_SUPPORT)
                .available(true)
                .build();
    }

    @Test
    @DisplayName("submitSurvey with all scores of 5 produces EXCELLENT wellbeing level")
    void submitSurvey_allHighScores_producesExcellentLevel() {
        SurveyResponse input = SurveyResponse.builder()
                .userId(UUID.randomUUID())
                .moodScore(5)
                .stressScore(5)
                .sleepScore(5)
                .socialScore(5)
                .academicScore(5)
                .build();

        when(surveyRepositoryPort.save(any(SurveyResponse.class))).thenAnswer(inv -> {
            SurveyResponse s = inv.getArgument(0);
            s.setId(UUID.randomUUID());
            return s;
        });

        SurveyResponse result = service.submitSurvey(input);

        assertThat(result.getAverageScore()).isEqualTo(5.0);
        assertThat(result.getWellbeingLevel()).isEqualTo(WellbeingLevel.EXCELLENT);
    }

    @Test
    @DisplayName("submitSurvey with all scores of 1 produces CRITICAL wellbeing level")
    void submitSurvey_allLowScores_producesCriticalLevel() {
        SurveyResponse input = SurveyResponse.builder()
                .userId(UUID.randomUUID())
                .moodScore(1)
                .stressScore(1)
                .sleepScore(1)
                .socialScore(1)
                .academicScore(1)
                .build();

        when(surveyRepositoryPort.save(any(SurveyResponse.class))).thenAnswer(inv -> {
            SurveyResponse s = inv.getArgument(0);
            s.setId(UUID.randomUUID());
            return s;
        });

        SurveyResponse result = service.submitSurvey(input);

        assertThat(result.getAverageScore()).isEqualTo(1.0);
        assertThat(result.getWellbeingLevel()).isEqualTo(WellbeingLevel.CRITICAL);
    }

    @Test
    @DisplayName("submitSurvey with mixed scores computes correct average and MODERATE level")
    void submitSurvey_mixedScores_computesCorrectAverage() {
        SurveyResponse input = SurveyResponse.builder()
                .userId(UUID.randomUUID())
                .moodScore(3)
                .stressScore(3)
                .sleepScore(3)
                .socialScore(3)
                .academicScore(4)
                .build();

        when(surveyRepositoryPort.save(any(SurveyResponse.class))).thenAnswer(inv -> {
            SurveyResponse s = inv.getArgument(0);
            s.setId(UUID.randomUUID());
            return s;
        });

        SurveyResponse result = service.submitSurvey(input);

        assertThat(result.getAverageScore()).isEqualTo(3.2);
        assertThat(result.getWellbeingLevel()).isEqualTo(WellbeingLevel.MODERATE);
    }

    @Test
    @DisplayName("getRecommendations with low mood score returns MENTAL_HEALTH resources")
    void getRecommendations_lowMoodScore_returnsMentalHealthResources() {
        when(resourceRepositoryPort.findByCategory(WellnessCategory.MENTAL_HEALTH))
                .thenReturn(List.of(mentalHealthResource));

        List<WellnessResource> recommendations = service.getRecommendations(2, 4, 4, 4, 4);

        assertThat(recommendations).hasSize(1);
        assertThat(recommendations.get(0).getCategory()).isEqualTo(WellnessCategory.MENTAL_HEALTH);
    }

    @Test
    @DisplayName("getRecommendations with low stress score returns MENTAL_HEALTH and SPORTS resources")
    void getRecommendations_lowStressScore_returnsMentalHealthAndSports() {
        when(resourceRepositoryPort.findByCategory(WellnessCategory.MENTAL_HEALTH))
                .thenReturn(List.of(mentalHealthResource));
        when(resourceRepositoryPort.findByCategory(WellnessCategory.SPORTS))
                .thenReturn(List.of(sportsResource));

        List<WellnessResource> recommendations = service.getRecommendations(4, 2, 4, 4, 4);

        assertThat(recommendations).hasSize(2);
        assertThat(recommendations).extracting(WellnessResource::getCategory)
                .containsExactlyInAnyOrder(WellnessCategory.MENTAL_HEALTH, WellnessCategory.SPORTS);
    }

    @Test
    @DisplayName("getRecommendations with low social and academic scores returns CULTURE and ACADEMIC_SUPPORT")
    void getRecommendations_lowSocialAndAcademic_returnsMultipleCategories() {
        when(resourceRepositoryPort.findByCategory(WellnessCategory.CULTURE))
                .thenReturn(List.of(cultureResource));
        when(resourceRepositoryPort.findByCategory(WellnessCategory.ACADEMIC_SUPPORT))
                .thenReturn(List.of(academicSupportResource));

        List<WellnessResource> recommendations = service.getRecommendations(4, 4, 4, 2, 2);

        assertThat(recommendations).hasSize(2);
        assertThat(recommendations).extracting(WellnessResource::getCategory)
                .containsExactlyInAnyOrder(WellnessCategory.CULTURE, WellnessCategory.ACADEMIC_SUPPORT);
    }

    @Test
    @DisplayName("getRecommendations with all high scores returns empty list")
    void getRecommendations_allHighScores_returnsEmptyList() {
        List<WellnessResource> recommendations = service.getRecommendations(5, 5, 5, 5, 5);

        assertThat(recommendations).isEmpty();
    }

    @Test
    @DisplayName("getSurveyHistory delegates to repository and returns results")
    void getSurveyHistory_delegatesToRepository() {
        UUID userId = UUID.randomUUID();
        SurveyResponse savedSurvey = SurveyResponse.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .moodScore(4)
                .stressScore(3)
                .sleepScore(4)
                .socialScore(3)
                .academicScore(4)
                .averageScore(3.6)
                .wellbeingLevel(WellbeingLevel.GOOD)
                .build();

        when(surveyRepositoryPort.findByUserId(userId)).thenReturn(List.of(savedSurvey));

        List<SurveyResponse> history = service.getSurveyHistory(userId);

        assertThat(history).hasSize(1);
        assertThat(history.get(0).getWellbeingLevel()).isEqualTo(WellbeingLevel.GOOD);
    }
}
