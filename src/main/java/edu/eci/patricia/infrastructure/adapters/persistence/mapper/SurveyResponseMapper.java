package edu.eci.patricia.infrastructure.adapters.persistence.mapper;

import edu.eci.patricia.domain.model.SurveyResponse;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.SurveyResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper that converts between {@link SurveyResponse} domain models
 * and {@link SurveyResponseEntity} JPA entities.
 */
@Component
public class SurveyResponseMapper {

    /**
     * Converts a JPA entity to a domain model.
     *
     * @param entity the JPA entity to convert
     * @return the corresponding domain model, or null if entity is null
     */
    public SurveyResponse toDomain(SurveyResponseEntity entity) {
        if (entity == null) {
            return null;
        }
        return SurveyResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .moodScore(entity.getMoodScore())
                .stressScore(entity.getStressScore())
                .sleepScore(entity.getSleepScore())
                .socialScore(entity.getSocialScore())
                .academicScore(entity.getAcademicScore())
                .averageScore(entity.getAverageScore())
                .wellbeingLevel(entity.getWellbeingLevel())
                .submittedAt(entity.getSubmittedAt())
                .build();
    }

    /**
     * Converts a domain model to a JPA entity.
     *
     * @param domain the domain model to convert
     * @return the corresponding JPA entity, or null if domain is null
     */
    public SurveyResponseEntity toEntity(SurveyResponse domain) {
        if (domain == null) {
            return null;
        }
        return SurveyResponseEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .moodScore(domain.getMoodScore())
                .stressScore(domain.getStressScore())
                .sleepScore(domain.getSleepScore())
                .socialScore(domain.getSocialScore())
                .academicScore(domain.getAcademicScore())
                .averageScore(domain.getAverageScore())
                .wellbeingLevel(domain.getWellbeingLevel())
                .submittedAt(domain.getSubmittedAt())
                .build();
    }
}
