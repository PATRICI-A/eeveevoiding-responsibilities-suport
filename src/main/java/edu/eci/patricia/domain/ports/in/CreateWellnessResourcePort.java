package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.domain.model.WellnessResource;

public interface CreateWellnessResourcePort {
    WellnessResource create(WellnessResource resource);
}