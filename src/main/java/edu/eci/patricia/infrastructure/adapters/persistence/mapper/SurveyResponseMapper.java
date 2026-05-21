package edu.eci.patricia.infrastructure.adapters.persistence.mapper;

import edu.eci.patricia.domain.model.SurveyResponse;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.SurveyResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper between {@link SurveyResponse} domain model and {@link SurveyResponseEntity} JPA entity.
 */
@Component
public class SurveyResponseMapper {

    public SurveyResponse toDomain(SurveyResponseEntity entity) {
        if (entity == null) return null;
        return SurveyResponse.builder()
                .id(entity.getId())
                .studentId(entity.getStudentId())
                .answers(entity.getAnswers())
                .submittedAt(entity.getSubmittedAt())
                .build();
    }

    public SurveyResponseEntity toEntity(SurveyResponse domain) {
        if (domain == null) return null;
        return SurveyResponseEntity.builder()
                .id(domain.getId())
                .studentId(domain.getStudentId())
                .answers(domain.getAnswers())
                .submittedAt(domain.getSubmittedAt())
                .build();
    }
}
