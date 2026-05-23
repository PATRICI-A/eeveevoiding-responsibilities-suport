package edu.eci.patricia.infrastructure.adapters.persistence.mapper;

import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.WellnessResourceEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;
/**
 * Mapper that converts between {@link WellnessResource} domain models
 * and {@link WellnessResourceEntity} JPA entities.
 */
@Component
public class WellnessResourceMapper {

    public WellnessResource toDomain(WellnessResourceEntity entity) {
        if (entity == null) {
            return null;
        }
        return WellnessResource.builder()
                .id(UUID.fromString(entity.getId()))
                .name(entity.getName())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .location(entity.getLocation())
                .contactInfo(entity.getContactInfo())
                .schedule(entity.getSchedule())
                .available(entity.isAvailable())
                .appointmentEmail(entity.getAppointmentEmail())
                .psychologistName(entity.getPsychologistName())
                .build();
    }

    public WellnessResourceEntity toEntity(WellnessResource domain) {
        if (domain == null) {
            return null;
        }
        return WellnessResourceEntity.builder()
                .id(domain.getId() != null ? domain.getId().toString() : UUID.randomUUID().toString())
                .name(domain.getName())
                .description(domain.getDescription())
                .category(domain.getCategory())
                .location(domain.getLocation())
                .contactInfo(domain.getContactInfo())
                .schedule(domain.getSchedule())
                .available(domain.isAvailable())
                .appointmentEmail(domain.getAppointmentEmail())
                .psychologistName(domain.getPsychologistName())
                .build();
    }
}
