package edu.eci.patricia.domain.ports.out;

import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.model.enums.WellnessCategory;
import edu.eci.patricia.domain.valueobjects.ResourceId;

import java.util.List;
import java.util.Optional;

public interface WellnessResourceRepositoryPort {
    List<WellnessResource> findAllActive(Optional<WellnessCategory> category);
    Optional<WellnessResource> findById(ResourceId id);
    WellnessResource save(WellnessResource resource);
    WellnessResource update(WellnessResource resource);
    void deleteById(ResourceId id);
}