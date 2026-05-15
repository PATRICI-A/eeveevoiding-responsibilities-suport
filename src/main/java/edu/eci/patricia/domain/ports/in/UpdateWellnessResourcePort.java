package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.valueobjects.ResourceId;

public interface UpdateWellnessResourcePort {
    WellnessResource update(ResourceId id, WellnessResource resource);
}