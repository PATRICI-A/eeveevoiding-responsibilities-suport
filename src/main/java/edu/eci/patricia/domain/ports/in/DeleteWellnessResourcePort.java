package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.domain.valueobjects.ResourceId;

public interface DeleteWellnessResourcePort {
    void delete(ResourceId id);
}