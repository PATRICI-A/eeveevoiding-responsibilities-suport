package edu.eci.patricia.application.usecase;

import edu.eci.patricia.domain.exceptions.WellnessResourceNotFoundException;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.model.enums.WellnessCategory;
import edu.eci.patricia.domain.ports.in.GetWellnessResourcePort;
import edu.eci.patricia.domain.ports.out.WellnessResourcePort;
import edu.eci.patricia.domain.valueobjects.WellnessResourceId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetWellnessResourceUseCase implements GetWellnessResourcePort {

    private final WellnessResourcePort wellnessResourcePort;

    @Override
    public List<WellnessResource> getAll(WellnessCategory category) {
        if (category == null) {
            return wellnessResourcePort.findAllActive();
        }
        return wellnessResourcePort.findActiveByCategory(category);
    }

    @Override
    public WellnessResource getById(UUID id) {
        return wellnessResourcePort.findById(WellnessResourceId.of(id))
                .orElseThrow(() -> new WellnessResourceNotFoundException(id.toString()));
    }
}