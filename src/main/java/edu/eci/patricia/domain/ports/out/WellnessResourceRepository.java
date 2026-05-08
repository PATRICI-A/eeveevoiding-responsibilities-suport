package edu.eci.patricia.domain.ports.out;

import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.model.enums.WellnessCategory;

import java.util.List;
import java.util.Optional;

public interface WellnessResourceRepository {
    List<WellnessResource> findAllActive();
    List<WellnessResource> findActiveByCategory(WellnessCategory category);
    Optional<WellnessResource> findById(String id);
}