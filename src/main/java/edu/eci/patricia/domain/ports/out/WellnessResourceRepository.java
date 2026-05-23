package edu.eci.patricia.domain.ports.out;

import edu.eci.patricia.domain.model.WellnessCategory;
import edu.eci.patricia.domain.model.WellnessResource;

import java.util.List;
import java.util.Optional;

/**
 * Output port for wellness resource queries (PTR23).
 */
public interface WellnessResourceRepository {

    List<WellnessResource> findAllActive();

    List<WellnessResource> findActiveByCategory(WellnessCategory category);

    Optional<WellnessResource> findById(String id);
}
