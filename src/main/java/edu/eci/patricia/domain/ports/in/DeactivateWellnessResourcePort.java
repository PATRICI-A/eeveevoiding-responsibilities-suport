package edu.eci.patricia.domain.ports.in;

import java.util.UUID;

public interface DeactivateWellnessResourcePort {
    void deactivate(UUID id);
}