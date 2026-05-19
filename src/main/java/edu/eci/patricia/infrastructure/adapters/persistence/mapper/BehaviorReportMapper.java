package edu.eci.patricia.infrastructure.adapters.persistence.mapper;

import edu.eci.patricia.domain.model.BehaviorReport;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.BehaviorReportEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper that converts between {@link BehaviorReport} domain models
 * and {@link BehaviorReportEntity} JPA entities.
 */
@Component
public class BehaviorReportMapper {

    /**
     * Converts a JPA entity to a domain model.
     *
     * @param entity the JPA entity to convert
     * @return the corresponding domain model, or null if entity is null
     */
    public BehaviorReport toDomain(BehaviorReportEntity entity) {
        if (entity == null) {
            return null;
        }
        return BehaviorReport.builder()
                .id(entity.getId())
                .reporterId(entity.getReporterId())
                .description(entity.getDescription())
                .location(entity.getLocation())
                .reportType(entity.getReportType())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Converts a domain model to a JPA entity.
     *
     * @param domain the domain model to convert
     * @return the corresponding JPA entity, or null if domain is null
     */
    public BehaviorReportEntity toEntity(BehaviorReport domain) {
        if (domain == null) {
            return null;
        }
        return BehaviorReportEntity.builder()
                .id(domain.getId())
                .reporterId(domain.getReporterId())
                .description(domain.getDescription())
                .location(domain.getLocation())
                .reportType(domain.getReportType())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
