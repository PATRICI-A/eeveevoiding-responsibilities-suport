package edu.eci.patricia.infrastructure.adapters.persistence.adapter;

import edu.eci.patricia.domain.model.SurveyResponse;
import edu.eci.patricia.domain.ports.out.SurveyResponseRepository;
import edu.eci.patricia.infrastructure.adapters.persistence.mapper.SurveyResponseMapper;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.JpaSurveyResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter that bridges {@link SurveyResponseRepository} (domain port) with the
 * JPA repository (infrastructure layer).
 */
@Component
@RequiredArgsConstructor
public class SurveyResponseRepositoryAdapter implements SurveyResponseRepository {

    private final JpaSurveyResponseRepository jpaRepository;
    private final SurveyResponseMapper mapper;

    @Override
    public void save(SurveyResponse survey) {
        jpaRepository.save(mapper.toEntity(survey));
    }

    @Override
    public Optional<SurveyResponse> findLatestByStudentId(String studentId) {
        return jpaRepository
                .findTopByStudentIdOrderBySubmittedAtDesc(studentId)
                .map(mapper::toDomain);
    }
}
