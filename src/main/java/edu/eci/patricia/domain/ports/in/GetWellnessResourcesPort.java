package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.model.enums.WellnessCategory;
import edu.eci.patricia.domain.valueobjects.ResourceId;

import java.util.List;
import java.util.Optional;

public interface GetWellnessResourcesPort {
    List<WellnessResource> getAll(Optional<WellnessCategory> category);
    WellnessResource getById(ResourceId id);
}