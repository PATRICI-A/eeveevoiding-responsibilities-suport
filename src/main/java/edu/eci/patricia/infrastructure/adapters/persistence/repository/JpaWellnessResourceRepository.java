package edu.eci.patricia.infrastructure.adapters.persistence.repository;

import edu.eci.patricia.domain.model.WellnessCategory;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.WellnessResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaWellnessResourceRepository extends JpaRepository<WellnessResourceEntity, String> {

    List<WellnessResourceEntity> findByAvailableTrue();

    List<WellnessResourceEntity> findByAvailableTrueAndCategory(WellnessCategory category);
}
