package edu.eci.patricia.infrastructure.adapters.persistence.adapter;

import edu.eci.patricia.domain.model.SurveyResponse;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.SurveyResponseEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.mapper.SurveyResponseMapper;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.JpaSurveyResponseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SurveyResponseRepositoryAdapterTest {

    @Mock
    private JpaSurveyResponseRepository jpaRepository;

    @Mock
    private SurveyResponseMapper mapper;

    @InjectMocks
    private SurveyResponseRepositoryAdapter adapter;

    private String studentId;
    private SurveyResponse domainSurvey;
    private SurveyResponseEntity entitySurvey;

    @BeforeEach
    void setUp() {
        studentId = "student-uuid-123";

        domainSurvey = SurveyResponse.builder()
                .id("survey-uuid-456")
                .studentId(studentId)
                .answers(Map.of("P01", "Bien", "P02", "3"))
                .submittedAt(LocalDateTime.now())
                .build();

        entitySurvey = SurveyResponseEntity.builder()
                .id("survey-uuid-456")
                .studentId(studentId)
                .answers(Map.of("P01", "Bien", "P02", "3"))
                .submittedAt(domainSurvey.getSubmittedAt())
                .build();
    }

    @Test
    @DisplayName("save persists survey via JPA")
    void save_persistsSurvey() {
        when(mapper.toEntity(domainSurvey)).thenReturn(entitySurvey);

        adapter.save(domainSurvey);

        verify(jpaRepository).save(entitySurvey);
    }

    @Test
    @DisplayName("findLatestByStudentId returns mapped domain when found")
    void findLatestByStudentId_returnsMappedDomain() {
        when(jpaRepository.findTopByStudentIdOrderBySubmittedAtDesc(studentId))
                .thenReturn(Optional.of(entitySurvey));
        when(mapper.toDomain(entitySurvey)).thenReturn(domainSurvey);

        Optional<SurveyResponse> result = adapter.findLatestByStudentId(studentId);

        assertThat(result).isPresent();
        assertThat(result.get().getStudentId()).isEqualTo(studentId);
        verify(jpaRepository).findTopByStudentIdOrderBySubmittedAtDesc(studentId);
    }

    @Test
    @DisplayName("findLatestByStudentId returns empty when no surveys")
    void findLatestByStudentId_noSurveys_returnsEmpty() {
        String unknownStudent = "unknown-student";
        when(jpaRepository.findTopByStudentIdOrderBySubmittedAtDesc(unknownStudent))
                .thenReturn(Optional.empty());

        Optional<SurveyResponse> result = adapter.findLatestByStudentId(unknownStudent);

        assertThat(result).isEmpty();
    }
}
