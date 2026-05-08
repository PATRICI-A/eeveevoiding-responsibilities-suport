package edu.eci.patricia.infrastructure.adapters.persistence.mapper;

import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.WellnessResourceEntity;
import org.springframework.stereotype.Component;

@Component
public class WellnessResourcePersistenceMapper {

    public WellnessResource toDomain(WellnessResourceEntity entity) {
        return WellnessResource.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .contactPhone(entity.getContactPhone())
                .contactEmail(entity.getContactEmail())
                .schedule(entity.getSchedule())
                .category(entity.getCategory())
                .active(entity.isActive())
                .appointmentEmail(entity.getAppointmentEmail())
                .psychologistName(entity.getPsychologistName())
                .build();
    }

    public WellnessResourceEntity toEntity(WellnessResource domain) {
        return WellnessResourceEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .contactPhone(domain.getContactPhone())
                .contactEmail(domain.getContactEmail())
                .schedule(domain.getSchedule())
                .category(domain.getCategory())
                .active(domain.isActive())
                .appointmentEmail(domain.getAppointmentEmail())
                .psychologistName(domain.getPsychologistName())
                .build();
    }
}