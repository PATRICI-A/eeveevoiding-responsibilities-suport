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

    public BehaviorReport toDomain(BehaviorReportEntity entity) {
        if (entity == null) {
            return null;
        }
        return BehaviorReport.builder()
                .id(entity.getId())
                .reporterId(entity.getReporterId())
                .reportType(entity.getReportType())
                .description(entity.getDescription())
                .referenceId(entity.getReferenceId())
                .status(entity.getStatus())
                .caseNumber(entity.getCaseNumber())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public BehaviorReportEntity toEntity(BehaviorReport domain) {
        if (domain == null) {
            return null;
        }
        return BehaviorReportEntity.builder()
                .id(domain.getId())
                .reporterId(domain.getReporterId())
                .reportType(domain.getReportType())
                .description(domain.getDescription())
                .referenceId(domain.getReferenceId())
                .status(domain.getStatus())
                .caseNumber(domain.getCaseNumber())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
