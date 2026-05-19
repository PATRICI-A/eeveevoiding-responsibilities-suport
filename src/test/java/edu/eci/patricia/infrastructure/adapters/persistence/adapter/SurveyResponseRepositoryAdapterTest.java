package edu.eci.patricia.infrastructure.adapters.persistence.adapter;

import edu.eci.patricia.domain.model.SurveyResponse;
import edu.eci.patricia.domain.model.WellbeingLevel;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.SurveyResponseEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.mapper.SurveyResponseMapper;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.SurveyResponseJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SurveyResponseRepositoryAdapter}.
 */
@ExtendWith(MockitoExtension.class)
class SurveyResponseRepositoryAdapterTest {

    @Mock
    private SurveyResponseJpaRepository jpaRepository;

    @Mock
    private SurveyResponseMapper mapper;

    @InjectMocks
    private SurveyResponseRepositoryAdapter adapter;

    private UUID userId;
    private SurveyResponse domainSurvey;
    private SurveyResponseEntity entitySurvey;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        domainSurvey = SurveyResponse.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .moodScore(4)
                .stressScore(3)
                .sleepScore(4)
                .socialScore(5)
                .academicScore(4)
                .averageScore(4.0)
                .wellbeingLevel(WellbeingLevel.GOOD)
                .submittedAt(LocalDateTime.now())
                .build();

        entitySurvey = SurveyResponseEntity.builder()
                .id(domainSurvey.getId())
                .userId(userId)
                .moodScore(4)
                .stressScore(3)
                .sleepScore(4)
                .socialScore(5)
                .academicScore(4)
                .averageScore(4.0)
                .wellbeingLevel(WellbeingLevel.GOOD)
                .submittedAt(domainSurvey.getSubmittedAt())
                .build();
    }

    @Test
    @DisplayName("save persists survey and returns mapped domain")
    void save_persistsAndReturnsDomain() {
        when(mapper.toEntity(domainSurvey)).thenReturn(entitySurvey);
        when(jpaRepository.save(entitySurvey)).thenReturn(entitySurvey);
        when(mapper.toDomain(entitySurvey)).thenReturn(domainSurvey);

        SurveyResponse saved = adapter.save(domainSurvey);

        assertThat(saved).isNotNull();
        assertThat(saved.getUserId()).isEqualTo(userId);
        verify(jpaRepository).save(entitySurvey);
    }

    @Test
    @DisplayName("findByUserId returns list of surveys ordered by date")
    void findByUserId_returnsMappedList() {
        when(jpaRepository.findByUserIdOrderBySubmittedAtDesc(userId))
                .thenReturn(List.of(entitySurvey));
        when(mapper.toDomain(entitySurvey)).thenReturn(domainSurvey);

        List<SurveyResponse> result = adapter.findByUserId(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
        verify(jpaRepository).findByUserIdOrderBySubmittedAtDesc(userId);
    }

    @Test
    @DisplayName("findByUserId returns empty list when no surveys exist for user")
    void findByUserId_noSurveys_returnsEmptyList() {
        UUID unknownUser = UUID.randomUUID();
        when(jpaRepository.findByUserIdOrderBySubmittedAtDesc(unknownUser))
                .thenReturn(List.of());

        List<SurveyResponse> result = adapter.findByUserId(unknownUser);

        assertThat(result).isEmpty();
    }
}
