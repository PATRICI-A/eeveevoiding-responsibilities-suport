package edu.eci.patricia.application.usecase;

import edu.eci.patricia.domain.exceptions.ResourceNotFoundException;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.ports.in.UpdateWellnessResourcePort;
import edu.eci.patricia.domain.ports.out.WellnessResourceRepositoryPort;
import edu.eci.patricia.domain.valueobjects.ResourceId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateWellnessResourceUseCase implements UpdateWellnessResourcePort {

    private final WellnessResourceRepositoryPort repository;

    @Override
    public WellnessResource update(ResourceId id, WellnessResource incoming) {
        WellnessResource existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id.toString()));

        existing.update(
                incoming.getName(),
                incoming.getDescription(),
                incoming.getContact(),
                incoming.getSchedule(),
                incoming.getPsychologistName(),
                incoming.getAppointmentEmail()
        );

        return repository.update(existing);
    }
}