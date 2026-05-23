package edu.eci.patricia.infrastructure.adapters.persistence.adapter;

import edu.eci.patricia.domain.model.WellnessCategory;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.ports.out.WellnessResourceRepository;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.WellnessResourceEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.JpaWellnessResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WellnessResourceRepositoryAdapter implements WellnessResourceRepository {

    private final JpaWellnessResourceRepository jpaRepository;

    @Override
    public List<WellnessResource> findAllActive() {
        return jpaRepository.findByAvailableTrue().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<WellnessResource> findActiveByCategory(WellnessCategory category) {
        return jpaRepository.findByAvailableTrueAndCategory(category).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<WellnessResource> findById(String id) {
        return jpaRepository.findById(id)
                .filter(WellnessResourceEntity::isAvailable)
                .map(this::toDomain);
    }

    private WellnessResource toDomain(WellnessResourceEntity e) {
        return WellnessResource.builder()
                .id(UUID.fromString(e.getId()))
                .name(e.getName())
                .description(e.getDescription())
                .category(e.getCategory())
                .location(e.getLocation())
                .contactInfo(e.getContactInfo())
                .schedule(e.getSchedule())
                .available(e.isAvailable())
                .appointmentEmail(e.getAppointmentEmail())
                .psychologistName(e.getPsychologistName())
                .build();
    }
}
