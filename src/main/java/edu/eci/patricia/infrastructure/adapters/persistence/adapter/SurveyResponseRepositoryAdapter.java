package edu.eci.patricia.infrastructure.adapters.persistence.adapter;

import edu.eci.patricia.domain.model.SurveyResponse;
import edu.eci.patricia.domain.ports.out.SurveyResponseRepositoryPort;
import edu.eci.patricia.infrastructure.adapters.persistence.mapper.SurveyResponseMapper;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.SurveyResponseJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter implementing {@link SurveyResponseRepositoryPort} via Spring Data JPA.
 */
@Component
@RequiredArgsConstructor
public class SurveyResponseRepositoryAdapter implements SurveyResponseRepositoryPort {

    private final SurveyResponseJpaRepository jpaRepository;
    private final SurveyResponseMapper mapper;

    /** {@inheritDoc} */
    @Override
    public SurveyResponse save(SurveyResponse surveyResponse) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(surveyResponse)));
    }

    /** {@inheritDoc} */
    @Override
    public List<SurveyResponse> findByUserId(UUID userId) {
        return jpaRepository.findByUserIdOrderBySubmittedAtDesc(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
