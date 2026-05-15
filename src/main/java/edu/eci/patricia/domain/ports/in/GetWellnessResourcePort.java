package edu.eci.patricia.domain.ports.in;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.model.enums.WellnessCategory;

import java.util.UUID;
import java.util.List;

public interface GetWellnessResourcePort {
    List<WellnessResource> getAll(WellnessCategory category);
    WellnessResource getById(UUID id);
}