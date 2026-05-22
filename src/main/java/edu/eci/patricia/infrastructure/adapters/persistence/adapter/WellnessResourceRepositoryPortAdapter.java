// WellnessResourceRepositoryPortAdapter.java (Nuevo archivo)
package edu.eci.patricia.infrastructure.adapters.persistence.adapter;

import edu.eci.patricia.domain.model.WellnessCategory;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.ports.out.WellnessResourceRepositoryPort;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.WellnessResourceEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.JpaWellnessResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WellnessResourceRepositoryPortAdapter implements WellnessResourceRepositoryPort {

    private final JpaWellnessResourceRepository jpaRepository;

    @Override
    public List<WellnessResource> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<WellnessResource> findByCategory(WellnessCategory category) {
        return jpaRepository.findByAvailableTrueAndCategory(category).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<WellnessResource> findById(UUID id) {
        return jpaRepository.findById(id.toString())
                .filter(WellnessResourceEntity::isAvailable)
                .map(this::toDomain);
    }

    @Override
    public WellnessResource save(WellnessResource resource) {
        WellnessResourceEntity entity = WellnessResourceEntity.builder()
                .id(resource.getId() != null ? resource.getId().toString() : UUID.randomUUID().toString())
                .name(resource.getName())
                .description(resource.getDescription())
                .category(resource.getCategory())
                .location(resource.getLocation())
                .contactInfo(resource.getContactInfo())
                .schedule(resource.getSchedule())
                .available(resource.isAvailable())
                .appointmentEmail(resource.getAppointmentEmail())
                .psychologistName(resource.getPsychologistName())
                .build();
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id.toString());
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id.toString());
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