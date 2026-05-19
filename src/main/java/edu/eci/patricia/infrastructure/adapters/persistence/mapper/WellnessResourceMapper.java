package edu.eci.patricia.infrastructure.adapters.persistence.mapper;

import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.WellnessResourceEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper that converts between {@link WellnessResource} domain models
 * and {@link WellnessResourceEntity} JPA entities.
 */
@Component
public class WellnessResourceMapper {

    /**
     * Converts a JPA entity to a domain model.
     *
     * @param entity the JPA entity to convert
     * @return the corresponding domain model, or null if entity is null
     */
    public WellnessResource toDomain(WellnessResourceEntity entity) {
        if (entity == null) {
            return null;
        }
        return WellnessResource.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .location(entity.getLocation())
                .contactInfo(entity.getContactInfo())
                .schedule(entity.getSchedule())
                .available(entity.isAvailable())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    /**
     * Converts a domain model to a JPA entity.
     *
     * @param domain the domain model to convert
     * @return the corresponding JPA entity, or null if domain is null
     */
    public WellnessResourceEntity toEntity(WellnessResource domain) {
        if (domain == null) {
            return null;
        }
        return WellnessResourceEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .category(domain.getCategory())
                .location(domain.getLocation())
                .contactInfo(domain.getContactInfo())
                .schedule(domain.getSchedule())
                .available(domain.isAvailable())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
