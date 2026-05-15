package edu.eci.patricia.application.usecase;

import edu.eci.patricia.domain.exceptions.WellnessResourceNotFoundException;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.ports.in.UpdateWellnessResourcePort;
import edu.eci.patricia.domain.ports.out.WellnessResourcePort;
import edu.eci.patricia.domain.valueobjects.WellnessResourceId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateWellnessResourceUseCase implements UpdateWellnessResourcePort {

    private final WellnessResourcePort wellnessResourcePort;

    @Override
    public WellnessResource update(UUID id, WellnessResource resource) {
        WellnessResourceId resourceId = WellnessResourceId.of(id);
        wellnessResourcePort.findById(resourceId)
                .orElseThrow(() -> new WellnessResourceNotFoundException(id.toString()));
        return wellnessResourcePort.save(resource);
    }
}