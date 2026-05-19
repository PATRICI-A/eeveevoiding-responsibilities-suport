package edu.eci.patricia.infrastructure.adapters.persistence.mapper;

import edu.eci.patricia.domain.model.SurveyResponse;
import edu.eci.patricia.domain.model.WellbeingLevel;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.SurveyResponseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link SurveyResponseMapper}.
 */
class SurveyResponseMapperTest {

    private SurveyResponseMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SurveyResponseMapper();
    }

    @Test
    @DisplayName("toDomain converts entity to domain model correctly")
    void toDomain_validEntity_returnsDomainModel() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        SurveyResponseEntity entity = SurveyResponseEntity.builder()
                .id(id)
                .userId(userId)
                .moodScore(4)
                .stressScore(3)
                .sleepScore(4)
                .socialScore(5)
                .academicScore(3)
                .averageScore(3.8)
                .wellbeingLevel(WellbeingLevel.GOOD)
                .submittedAt(now)
                .build();

        SurveyResponse domain = mapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getUserId()).isEqualTo(userId);
        assertThat(domain.getMoodScore()).isEqualTo(4);
        assertThat(domain.getStressScore()).isEqualTo(3);
        assertThat(domain.getSleepScore()).isEqualTo(4);
        assertThat(domain.getSocialScore()).isEqualTo(5);
        assertThat(domain.getAcademicScore()).isEqualTo(3);
        assertThat(domain.getAverageScore()).isEqualTo(3.8);
        assertThat(domain.getWellbeingLevel()).isEqualTo(WellbeingLevel.GOOD);
        assertThat(domain.getSubmittedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("toDomain returns null when entity is null")
    void toDomain_nullEntity_returnsNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    @DisplayName("toEntity converts domain model to entity correctly")
    void toEntity_validDomain_returnsEntity() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        SurveyResponse domain = SurveyResponse.builder()
                .id(id)
                .userId(userId)
                .moodScore(1)
                .stressScore(1)
                .sleepScore(2)
                .socialScore(1)
                .academicScore(2)
                .averageScore(1.4)
                .wellbeingLevel(WellbeingLevel.CRITICAL)
                .submittedAt(now)
                .build();

        SurveyResponseEntity entity = mapper.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getUserId()).isEqualTo(userId);
        assertThat(entity.getMoodScore()).isEqualTo(1);
        assertThat(entity.getAverageScore()).isEqualTo(1.4);
        assertThat(entity.getWellbeingLevel()).isEqualTo(WellbeingLevel.CRITICAL);
        assertThat(entity.getSubmittedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("toEntity returns null when domain is null")
    void toEntity_nullDomain_returnsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    @DisplayName("Round-trip entity→domain→entity preserves all scores")
    void roundTrip_entityToDomainToEntity_preservesScores() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        SurveyResponseEntity original = SurveyResponseEntity.builder()
                .id(id)
                .userId(userId)
                .moodScore(5)
                .stressScore(5)
                .sleepScore(5)
                .socialScore(5)
                .academicScore(5)
                .averageScore(5.0)
                .wellbeingLevel(WellbeingLevel.EXCELLENT)
                .submittedAt(LocalDateTime.now())
                .build();

        SurveyResponseEntity roundTripped = mapper.toEntity(mapper.toDomain(original));

        assertThat(roundTripped.getMoodScore()).isEqualTo(5);
        assertThat(roundTripped.getAverageScore()).isEqualTo(5.0);
        assertThat(roundTripped.getWellbeingLevel()).isEqualTo(WellbeingLevel.EXCELLENT);
    }
}
