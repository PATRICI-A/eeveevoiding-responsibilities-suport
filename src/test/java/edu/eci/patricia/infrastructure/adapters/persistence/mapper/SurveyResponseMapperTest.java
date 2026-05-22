package edu.eci.patricia.infrastructure.adapters.persistence.mapper;

import edu.eci.patricia.domain.model.SurveyResponse;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.SurveyResponseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SurveyResponseMapperTest {

    private SurveyResponseMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SurveyResponseMapper();
    }

    @Test
    @DisplayName("toDomain converts entity to domain model correctly")
    void toDomain_validEntity_returnsDomainModel() {
        String id = "550e8400-e29b-41d4-a716-446655440000";
        String studentId = "student-123";
        LocalDateTime now = LocalDateTime.now();
        Map<String, String> answers = Map.of("P01", "Bien", "P02", "3");

        SurveyResponseEntity entity = SurveyResponseEntity.builder()
                .id(id)
                .studentId(studentId)
                .answers(answers)
                .submittedAt(now)
                .build();

        SurveyResponse domain = mapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getStudentId()).isEqualTo(studentId);
        assertThat(domain.getAnswers()).isEqualTo(answers);
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
        String id = "660e8400-e29b-41d4-a716-446655440001";
        String studentId = "student-456";
        LocalDateTime now = LocalDateTime.now();
        Map<String, String> answers = Map.of(
                "P01", "Mal", "P02", "5", "P03", "Nunca",
                "P04", "Mala", "P05", "Casi nunca", "P06", "No mucho",
                "P07", "Rara vez", "P08", "2", "P09", "Con frecuencia",
                "P10", "Manejo del estrés"
        );

        SurveyResponse domain = SurveyResponse.builder()
                .id(id)
                .studentId(studentId)
                .answers(answers)
                .submittedAt(now)
                .build();

        SurveyResponseEntity entity = mapper.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getStudentId()).isEqualTo(studentId);
        assertThat(entity.getAnswers()).isEqualTo(answers);
        assertThat(entity.getSubmittedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("toEntity returns null when domain is null")
    void toEntity_nullDomain_returnsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    @DisplayName("Round-trip preserves all data")
    void roundTrip_preservesData() {
        String id = "770e8400-e29b-41d4-a716-446655440002";
        String studentId = "student-789";
        Map<String, String> answers = Map.of("P01", "Bien", "P09", "A veces");

        SurveyResponseEntity original = SurveyResponseEntity.builder()
                .id(id)
                .studentId(studentId)
                .answers(answers)
                .submittedAt(LocalDateTime.now())
                .build();

        SurveyResponseEntity roundTripped = mapper.toEntity(mapper.toDomain(original));

        assertThat(roundTripped.getId()).isEqualTo(id);
        assertThat(roundTripped.getStudentId()).isEqualTo(studentId);
        assertThat(roundTripped.getAnswers()).isEqualTo(answers);
    }
}
