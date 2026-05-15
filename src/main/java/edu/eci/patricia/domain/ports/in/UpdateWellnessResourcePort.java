package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.domain.model.WellnessResource;
import java.util.UUID;

public interface UpdateWellnessResourcePort {
    WellnessResource update(UUID id, WellnessResource resource);
}