package edu.eci.patricia.infrastructure.adapters.persistence.repository;

import edu.eci.patricia.domain.model.enums.WellnessCategory;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.WellnessResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WellnessResourceRepository extends JpaRepository<WellnessResourceEntity, UUID> {
    List<WellnessResourceEntity> findAllByActiveTrue();
    List<WellnessResourceEntity> findAllByActiveTrueAndCategory(WellnessCategory category);
}