package edu.eci.patricia.application.usecase;

import edu.eci.patricia.domain.exceptions.MissingMentalHealthFieldsException;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.ports.in.CreateWellnessResourcePort;
import edu.eci.patricia.domain.ports.out.WellnessResourcePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateWellnessResourceUseCase implements CreateWellnessResourcePort {

    private final WellnessResourcePort wellnessResourcePort;

    @Override
    public WellnessResource create(WellnessResource resource) {
        return wellnessResourcePort.save(resource);
    }
}