package edu.eci.patricia.application.usecase;

import edu.eci.patricia.domain.exceptions.WellnessResourceNotFoundException;
import edu.eci.patricia.domain.ports.in.DeactivateWellnessResourcePort;
import edu.eci.patricia.domain.ports.out.WellnessResourcePort;
import edu.eci.patricia.domain.valueobjects.WellnessResourceId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeactivateWellnessResourceUseCase implements DeactivateWellnessResourcePort {

    private final WellnessResourcePort wellnessResourcePort;

    @Override
    public void deactivate(UUID id) {
        WellnessResourceId resourceId = WellnessResourceId.of(id);
        wellnessResourcePort.findById(resourceId)
                .orElseThrow(() -> new WellnessResourceNotFoundException(id.toString()));
        wellnessResourcePort.deactivate(resourceId);
    }
}